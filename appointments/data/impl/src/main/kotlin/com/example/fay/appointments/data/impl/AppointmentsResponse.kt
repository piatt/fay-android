package com.example.fay.appointments.data.impl

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentsResponse(val appointments: List<AppointmentResponse>)