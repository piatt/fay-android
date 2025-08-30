package com.example.fay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fay.auth.data.api.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FayAppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FayAppState())
    val state: StateFlow<FayAppState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FayAppState()
    )

    init {
        viewModelScope.launch {
            authRepository.authenticated.collect { authenticated ->
                _state.update {
                    it.copy(authenticated = authenticated)
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}