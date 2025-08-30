package com.example.fay.appointments.domain

data class AppointmentState(
    val id: String,
    val description: String,
    val formattedMonth: String,
    val formattedDay: String,
    val formattedTimeRange: String
)