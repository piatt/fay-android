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
        val authToken = runBlocking { authRepository.getAuthToken() }
        return authToken?.let {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
            chain.proceed(authenticatedRequest)
        } ?: chain.proceed(originalRequest)
    }
}