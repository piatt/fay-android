package com.example.fay.appointments.data.api

import java.time.ZonedDateTime

data class Appointment(
    val id: String,
    val patientId: String,
    val providerId: String,
    val providerName: String,
    val status: AppointmentStatus,
    val type: String,
    val recurrenceType: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val duration: Int
)