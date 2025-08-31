package com.example.fay.auth.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that automatically adds authentication headers to outgoing requests.
 * Retrieves the auth token from the repository and includes it as a Bearer token in the Authorization header.
 */
class AuthInterceptor(private val authRepository: AuthRepository) : Interceptor {
    /**
     * Intercepts HTTP requests and adds authentication header if auth token is available.
     * 
     * @param chain The interceptor chain
     * @return Response from the intercepted request with auth header added if token exists
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authToken = try {
            runBlocking { authRepository.getAuthToken() }
        } catch (_: Exception) {
            null
        }
        return if (authToken != null && authToken.isNotEmpty()) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}