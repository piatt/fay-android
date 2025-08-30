package com.example.fay.appointments.data.impl

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentStatus

fun AppointmentResponse.toAppointment(): Appointment? {
    return if (appointment_id != null) {
        Appointment(
            id = appointment_id,
            patientId = patient_id ?: "",
            providerId = provider_id ?: "",
            providerName = PROVIDER_NAME,
            status = status.toAppointmentStatus(),
            type = appointment_type ?: "",
            recurrenceType = recurrence_type ?: "",
            start = start ?: "",
            end = end ?: "",
            duration = duration_in_minutes ?: 0
        )
    } else null
}

private fun String?.toAppointmentStatus(): AppointmentStatus {
    return when (this) {
        "Scheduled" -> AppointmentStatus.UPCOMING
        "Occurred" -> AppointmentStatus.PAST
        else -> AppointmentStatus.UNKNOWN
    }
}

private const val PROVIDER_NAME = "Jane Williams, RD"