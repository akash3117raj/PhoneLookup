package com.phonelookup.app.data.api

import com.phonelookup.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface.
 *
 * Configure your API base URL in RetrofitClient.kt.
 * Endpoints should be adjusted to match your backend.
 */
interface ApiService {

    /** Authenticate user and receive session token */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /** Lookup phone number details */
    @GET("api")
    suspend fun lookupPhone(
        @Query("key") key: String = "MANI",
        @Query("type") type: String = "mobile",
        @Query("term") phoneNumber: String
    ): Response<ApiResponse>

    /** Lookup Aadhar/Family details */
    @GET("api")
    suspend fun lookupFamily(
        @Query("key") key: String = "MANI",
        @Query("type") type: String = "id_family",
        @Query("term") aadharNumber: String
    ): Response<FamilyLookupResponse>
}
