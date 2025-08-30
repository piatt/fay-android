package com.example.fay.auth.data.impl

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("signin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}