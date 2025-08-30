package com.example.fay.auth.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authRepository: AuthRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return authRepository.getAuthToken()?.let {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
            chain.proceed(authenticatedRequest)
        } ?: chain.proceed(originalRequest)
    }
}