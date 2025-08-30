package com.example.fay.auth.data.api

import com.example.fay.core.common.result.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authenticated: Flow<Boolean>
    fun getAuthToken(): String?
    fun login(email: String, password: String): Flow<Resource<Boolean>>
    fun logout()
}