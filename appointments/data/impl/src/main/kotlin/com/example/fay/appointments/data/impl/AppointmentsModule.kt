package com.example.fay.appointments.data.impl

import com.example.fay.appointments.data.api.AppointmentsRepository
import com.example.fay.core.common.DispatcherProvider
import com.example.fay.core.network.AuthenticatedRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppointmentsModule {
    @Singleton
    @Provides
    fun provideAppointmentApiService(
        @AuthenticatedRetrofit retrofit: Retrofit
    ) : AppointmentsApiService {
        return retrofit.create(AppointmentsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppointmentRepository(
        apiService: AppointmentsApiService,
        dispatcherProvider: DispatcherProvider
    ) : AppointmentsRepository {
        return DefaultAppointmentsRepository(apiService, dispatcherProvider)
    }
}