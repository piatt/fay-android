package com.example.fay.appointments.data.api

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: String,
    val patientId: String,
    val providerId: String,
    val providerName: String,
    val status: AppointmentStatus,
    val type: String,
    val recurrenceType: String,
    val start: String,
    val end: String,
    val duration: Int
)