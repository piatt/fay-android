package com.example.fay.appointments.domain

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentStatus
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.filter

fun getFilteredAppointmentsUseCase(
    appointments: List<Appointment>,
    status: AppointmentStatus
): List<AppointmentState> {
    return appointments
        .filter { it.status == status }
        .map { it.toAppointmentState() }
}

private fun Appointment.toAppointmentState(): AppointmentState {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val formattedMonth = start.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
    val formattedDay = start.dayOfMonth.toString()
    val formattedTimeRange = "${start.format(timeFormatter)} - ${end.format(timeFormatter)} (PT)"

    return AppointmentState(
        id = id,
        description = "$type with $providerName",
        formattedMonth = formattedMonth,
        formattedDay = formattedDay,
        formattedTimeRange = formattedTimeRange
    )
}