package com.example.fay.appointments.data.api

import java.time.LocalDateTime

data class Appointment(
    val id: String,
    val patientId: String,
    val providerId: String,
    val providerName: String,
    val status: AppointmentStatus,
    val type: String,
    val recurrenceType: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val duration: Int
)