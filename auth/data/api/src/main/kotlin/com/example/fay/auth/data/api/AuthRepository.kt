package com.example.fay.auth.data.api

import com.example.fay.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authenticated: Flow<Boolean>
    suspend fun getAuthToken(): String?
    fun login(email: String, password: String): Flow<Resource<Boolean>>
    suspend fun logout()
}