package com.phonelookup.app.data.repository

import com.phonelookup.app.data.api.RetrofitClient
import com.phonelookup.app.data.model.ApiResponse
import com.phonelookup.app.data.model.FamilyLookupResponse
import com.phonelookup.app.data.model.LoginRequest
import com.phonelookup.app.data.model.LoginResponse
import com.phonelookup.app.data.model.PhoneResult
import com.phonelookup.app.native_bridge.NativeBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Proxy

/**
 * Repository layer — single source of truth for all data operations.
 * Uses coroutines on IO dispatcher for non-blocking network calls.
 * Includes native C++ result caching via FNV-1a hash.
 */
class PhoneRepository {

    private val api = RetrofitClient.apiService
    
    // Create client that ignores system proxies to avoid "10.93..." connection errors
    private val client = OkHttpClient.Builder()
        .proxy(Proxy.NO_PROXY)
        .build()

    // In-memory LRU cache for instant re-lookups
    private val resultCache = LinkedHashMap<Long, PhoneResult>(16, 0.75f, true)

    /** Validate license key against GitHub raw file */
    suspend fun validateLicenseKey(userKey: String): Result<Boolean> {
        // Emergency offline key for testing
        if (userKey == "MANI-TEST-786") return Result.success(true)

        return withContext(Dispatchers.IO) {
            try {
                val url = "https://raw.githubusercontent.com/studywithsunny17744-svg/Apk-password-/main/keys.txt"
                val request = Request.Builder()
                    .url(url)
                    .header("Cache-Control", "no-cache")
                    .build()
                
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))
                    
                    val content = response.body?.string() ?: ""
                    val keys = content.split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    
                    Result.success(keys.contains(userKey.trim()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /** Authenticate user and return login response */
    suspend fun login(licenseKey: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(licenseKey = licenseKey))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login failed"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /** Lookup Aadhar/Family details */
    suspend fun lookupFamily(aadharNumber: String): Result<PhoneResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.lookupFamily(aadharNumber = aadharNumber)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse: FamilyLookupResponse = response.body()!!
                    val info = apiResponse.result.results.firstOrNull()

                    if (info != null) {
                        val membersList = info.members.joinToString("\n") { 
                            "• ${it.memberName} (${it.memberId})" 
                        }
                        
                        val phoneResult = PhoneResult(
                            number = info.details.rationCardNo ?: aadharNumber,
                            name = "Family: ${info.details.districtName ?: "Records"}",
                            carrier = info.details.stateName ?: "Unknown",
                            location = info.details.schemeName ?: "Unknown",
                            lineType = membersList,
                            countryCode = "IN",
                            valid = true
                        )
                        Result.success(phoneResult)
                    } else {
                        Result.failure(Exception("No family records found"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Family lookup failed"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /** Lookup phone number with native-optimized caching */
    suspend fun lookupPhone(phoneNumber: String): Result<PhoneResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Clean phone number using native C++ function
                val cleaned = NativeBridge.cleanPhoneNumber(phoneNumber)

                // Check cache using native FNV-1a hash
                val cacheKey = NativeBridge.fastHash(cleaned)
                resultCache[cacheKey]?.let { cached ->
                    return@withContext Result.success(cached)
                }

                val response = api.lookupPhone(phoneNumber = cleaned)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val contact = apiResponse.result.results.firstOrNull()

                    if (contact != null) {
                        val phoneResult = PhoneResult(
                            number = contact.mobile ?: cleaned,
                            name = contact.name ?: "Unknown",
                            carrier = contact.circle ?: "Unknown",
                            location = contact.address?.replace("!", ", ") ?: "Unknown",
                            lineType = contact.alt ?: "Mobile",
                            countryCode = "IN",
                            valid = true
                        )
                        // Cache result
                        if (resultCache.size > 50) {
                            resultCache.remove(resultCache.keys.first())
                        }
                        resultCache[cacheKey] = phoneResult
                        Result.success(phoneResult)
                    } else {
                        Result.failure(Exception("No records found for this number"))
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Lookup failed"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /** Format phone result using native C++ string processing */
    fun formatResultForCopy(result: PhoneResult): String {
        return NativeBridge.formatPhoneResult(
            number = result.number,
            name = result.name,
            carrier = result.carrier,
            location = result.location,
            lineType = result.lineType,
            countryCode = result.countryCode
        )
    }

    /** Clear cached results */
    fun clearCache() {
        resultCache.clear()
    }
}
