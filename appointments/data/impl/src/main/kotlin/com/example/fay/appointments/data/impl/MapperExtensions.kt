package com.example.fay.appointments.data.impl

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
            start = start.toZonedDateTime(),
            end = end.toZonedDateTime(),
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

private fun String?.toZonedDateTime(): ZonedDateTime {
    return if (this.isNullOrBlank()) {
        ZonedDateTime.now()
    } else {
        try {
            val zonedDateTime = ZonedDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
            zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        } catch (_: Exception) {
            ZonedDateTime.now()
        }
    }
}

private const val PROVIDER_NAME = "Jane Williams, RD"