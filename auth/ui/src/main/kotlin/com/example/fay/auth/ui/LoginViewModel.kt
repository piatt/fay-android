package com.example.fay.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fay.auth.data.api.AuthRepository
import com.example.fay.core.common.result.Resource
import com.example.fay.core.network.NoNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = LoginUiState()
    )

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { resource ->
                _uiState.update {
                    when (resource) {
                        is Resource.Loading -> LoginUiState(isLoading = true)
                        is Resource.Success -> LoginUiState(authenticated = true)
                        is Resource.Error -> LoginUiState(errorMessage = resource.message)
                        is Resource.Exception -> {
                            val errorMessage = when (resource.exception) {
                                is NoNetworkException -> "No network connection"
                                else -> "An error occurred: ${resource.exception.message}"
                            }
                            LoginUiState(errorMessage = errorMessage)
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}