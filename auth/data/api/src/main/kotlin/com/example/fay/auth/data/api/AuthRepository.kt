package com.example.fay.auth.data.api

import com.example.fay.core.common.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authenticated: Flow<Boolean>
    fun getAuthToken(): String?
    suspend fun login(email: String, password: String): Flow<Resource<Boolean>>
    fun logout()
}