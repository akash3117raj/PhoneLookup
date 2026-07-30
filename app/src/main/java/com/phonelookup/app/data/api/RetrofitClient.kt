package com.phonelookup.app.data.api

import com.phonelookup.app.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client with aggressive timeout tuning
 * and connection pooling for ultra-fast API responses.
 *
 * ⚠️ CONFIGURE YOUR API BASE URL BELOW ⚠️
 */
object RetrofitClient {

    // ── Change this to your actual API base URL ──────────────
    private const val BASE_URL = "https://api.subhxcosmo.in/"

    private lateinit var retrofit: Retrofit

    fun init(sessionManager: SessionManager) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY) // Force ignore system proxies
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
