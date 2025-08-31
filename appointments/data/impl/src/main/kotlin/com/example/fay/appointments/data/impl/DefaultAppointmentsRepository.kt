package com.example.fay.appointments.data.impl

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentsRepository
import com.example.fay.core.common.DispatcherProvider
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject

class DefaultAppointmentsRepository @Inject constructor(
    private val apiService: AppointmentsApiService,
    private val dispatcherProvider: DispatcherProvider
): AppointmentsRepository {
    /**
     * Retrieves appointments from the remote API.
     * 
     * @return Flow that emits Resource states:
     *   - Loading: Emitted initially while the request is in progress
     *   - Success: Contains the list of appointments if the request succeeds
     *   - Error: Contains HTTP error code and message if the request fails
     *   - Exception: Contains the exception if a network or other error occurs
     */
    override fun getAppointments(): Flow<Resource<List<Appointment>>> = flow {
        emit(Resource.Loading)
        val resource = try {
            val response = apiService.getAppointments()
            if (response.isSuccessful && response.body() != null) {
                val appointments = response.body()?.appointments
                    ?.mapNotNull { it.toAppointment() }
                    ?: emptyList()
                Resource.Success(appointments)
            } else {
                Resource.Error(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: HttpException) {
            Resource.Error(e.code(), e.message())
        } catch (e: NoNetworkException) {
            Resource.Exception(e)
        } catch (e: Throwable) {
            Resource.Exception(e)
        }
        emit(resource)
    }.flowOn(dispatcherProvider.io)
}