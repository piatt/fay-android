package com.example.fay.auth.data.impl

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String)