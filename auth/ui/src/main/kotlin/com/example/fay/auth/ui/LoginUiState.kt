package com.example.fay.auth.ui

data class LoginUiState(
    val loading: Boolean = false,
    val authenticated: Boolean = false,
    val errorMessage: String? = null
)