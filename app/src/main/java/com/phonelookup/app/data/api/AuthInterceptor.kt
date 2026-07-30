package com.phonelookup.app.data.api

import com.phonelookup.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that injects the Bearer auth token
 * into every outgoing request automatically.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.authToken
        val host = originalRequest.url.host

        // Don't add auth header for the new public API or GitHub
        val request = if (!token.isNullOrEmpty() && !host.contains("subhxcosmo") && !host.contains("github")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
        }

        return chain.proceed(request)
    }
}
