package com.example.fay.appointments.domain

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentStatus
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun getFilteredAppointmentsUseCase(
    appointments: List<Appointment>,
    status: AppointmentStatus
): List<AppointmentState> {
    return appointments
        .filter { it.status == status }
        .map { it.toAppointmentState() }
}

/**
 * Converts an Appointment domain object to AppointmentState for UI display.
 * Formats the appointment with local timezone-adjusted time ranges and readable date components.
 * 
 * @return AppointmentState with formatted month, day, and time range including timezone abbreviation
 */
private fun Appointment.toAppointmentState(): AppointmentState {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val timezoneAbbreviation = start.zone.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val formattedMonth = start.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
    val formattedDay = start.dayOfMonth.toString()
    val formattedTimeRange = "${start.format(timeFormatter)} - ${end.format(timeFormatter)} ($timezoneAbbreviation)"

    return AppointmentState(
        id = id,
        description = "$type with $providerName",
        formattedMonth = formattedMonth,
        formattedDay = formattedDay,
        formattedTimeRange = formattedTimeRange
    )
}