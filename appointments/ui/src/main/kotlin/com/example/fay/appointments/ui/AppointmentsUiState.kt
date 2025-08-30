package com.example.fay.appointments.ui

import com.example.fay.appointments.domain.AppointmentState

data class AppointmentsUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val upcomingAppointments: List<AppointmentState> = emptyList(),
    val pastAppointments: List<AppointmentState> = emptyList()
)