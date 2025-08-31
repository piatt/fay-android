package com.example.fay.appointments.data.api

import com.example.fay.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface AppointmentsRepository {
    fun getAppointments(): Flow<Resource<List<Appointment>>>
}