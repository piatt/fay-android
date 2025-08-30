package com.example.fay.appointments.data.impl

import retrofit2.Response
import retrofit2.http.GET

interface AppointmentsApiService {
    @GET("appointments")
    suspend fun getAppointments(): Response<AppointmentsResponse>
}