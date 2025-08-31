package com.example.fay.appointments.domain

import com.example.fay.appointments.data.api.Appointment

/**
 * @see Appointment.toAppointmentState
 */
data class AppointmentState(
    val id: String,
    val description: String,
    val formattedMonth: String,
    val formattedDay: String,
    val formattedTimeRange: String
)