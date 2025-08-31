package com.example.fay.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fay.appointments.data.api.AppointmentStatus
import com.example.fay.appointments.data.api.AppointmentsRepository
import com.example.fay.appointments.domain.getFilteredAppointmentsUseCase
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState.onStart {
        refresh()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AppointmentsUiState()
    )

    fun refresh() {
        viewModelScope.launch {
            appointmentsRepository.getAppointments().collect { resource ->
                _uiState.update {
                    when (resource) {
                        is Resource.Loading -> AppointmentsUiState(loading = true)
                        is Resource.Success -> {
                            AppointmentsUiState(
                                upcomingAppointments = getFilteredAppointmentsUseCase(
                                    appointments = resource.data,
                                    status = AppointmentStatus.UPCOMING
                                ),
                                pastAppointments = getFilteredAppointmentsUseCase(
                                    appointments = resource.data,
                                    status = AppointmentStatus.PAST
                                )
                            )
                        }
                        is Resource.Error -> AppointmentsUiState(errorMessage = resource.message)
                        is Resource.Exception -> {
                            val errorMessage = when (resource.exception) {
                                is NoNetworkException -> "No network connection"
                                else -> "An error occurred: ${resource.exception.message}"
                            }
                            AppointmentsUiState(errorMessage = errorMessage)
                        }
                    }
                }
            }
        }
    }
}