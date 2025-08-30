package com.example.fay.appointments.data.impl

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentResponse(
    val appointment_id: String?,
    val patient_id: String?,
    val provider_id: String?,
    val status: String?,
    val appointment_type: String?,
    val start: String?,
    val end: String?,
    val duration_in_minutes: Int?,
    val recurrence_type: String?
)