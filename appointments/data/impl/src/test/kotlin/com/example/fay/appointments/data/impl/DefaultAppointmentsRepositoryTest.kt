package com.example.fay.appointments.data.impl

import app.cash.turbine.test
import com.example.fay.appointments.data.api.Appointment
import com.example.fay.core.common.DispatcherProvider
import com.example.fay.core.data.Resource
import com.example.fay.core.network.NoNetworkException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultAppointmentsRepositoryTest {
    private lateinit var apiService: AppointmentsApiService
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var repository: DefaultAppointmentsRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        apiService = mockk()
        dispatcherProvider = mockk {
            every { io } returns testDispatcher
        }
        
        repository = DefaultAppointmentsRepository(apiService, dispatcherProvider)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAppointments successful response with valid appointments`() = runTest {
        val appointmentResponses = listOf(
            createValidAppointmentResponse("1"),
            createValidAppointmentResponse("2")
        )
        val response = Response.success(AppointmentsResponse(appointmentResponses))
        
        coEvery { apiService.getAppointments() } returns response

        repository.getAppointments().test {
            assertEquals(Resource.Loading, awaitItem())
            
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(2, result.data.size)
            assertEquals("1", result.data[0].id)
            assertEquals("2", result.data[1].id)
            
            awaitComplete()
        }
    }

    @Test
    fun `getAppointments successful response with mixed valid and invalid appointments`() = runTest {
        val appointmentResponses = listOf(
            createValidAppointmentResponse("1"),
            createInvalidAppointmentResponse(), // null appointment_id
            createValidAppointmentResponse("3")
        )
        val response = Response.success(AppointmentsResponse(appointmentResponses))
        
        coEvery { apiService.getAppointments() } returns response

        repository.getAppointments().test {
            assertEquals(Resource.Loading, awaitItem())
            
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            // Should only contain valid appointments (invalid ones filtered out)
            assertEquals(2, result.data.size)
            assertEquals("1", result.data[0].id)
            assertEquals("3", result.data[1].id)
            
            awaitComplete()
        }
    }

    @Test
    fun `getAppointments successful response with empty appointments list`() = runTest {
        val response = Response.success(AppointmentsResponse(emptyList()))
        
        coEvery { apiService.getAppointments() } returns response

        repository.getAppointments().test {
            assertEquals(Resource.Loading, awaitItem())
            
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertTrue(result.data.isEmpty())
            
            awaitComplete()
        }
    }

    @Test
    fun `getAppointments successful response with null body`() = runTest {
        val response = Response.success<AppointmentsResponse>(null)
        
        coEvery { apiService.getAppointments() } returns response

        repository.getAppointments().test {
            assertEquals(Resource.Loading, awaitItem())
            
            val result = awaitItem()
            assertTrue(result is Resource.Error)
            assertEquals(200, result.code)
            
            awaitComplete()
        }
    }

    @TestFactory
    fun `getAppointments error response scenarios`() = listOf(
        ErrorTestCase(
            name = "404 Not Found error",
            response = Response.error<AppointmentsResponse>(
                404, 
                "Not Found".toResponseBody()
            ),
            expectedCode = 404,
            expectedMessage = "Response.error()"
        ),
        ErrorTestCase(
            name = "500 Internal Server Error",
            response = Response.error<AppointmentsResponse>(
                500, 
                "Internal Server Error".toResponseBody()
            ),
            expectedCode = 500,
            expectedMessage = "Response.error()"
        ),
        ErrorTestCase(
            name = "401 Unauthorized error",
            response = Response.error<AppointmentsResponse>(
                401, 
                "Unauthorized".toResponseBody()
            ),
            expectedCode = 401,
            expectedMessage = "Response.error()"
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            runTest {
                coEvery { apiService.getAppointments() } returns testCase.response

                repository.getAppointments().test {
                    assertEquals(Resource.Loading, awaitItem())
                    
                    val result = awaitItem()
                    assertTrue(result is Resource.Error)
                    assertEquals(testCase.expectedCode, result.code)
                    assertEquals(testCase.expectedMessage, result.message)
                    
                    awaitComplete()
                }
            }
        }
    }

    @TestFactory
    fun `getAppointments exception scenarios`() = listOf(
        ExceptionTestCase(
            name = "HttpException during API call",
            exception = HttpException(Response.error<Any>(404, "Not Found".toResponseBody())),
            expectedResource = Resource.Error(404, "HTTP 404 ")
        ),
        ExceptionTestCase(
            name = "NoNetworkException during API call",
            exception = NoNetworkException(),
            expectedResource = Resource.Exception(NoNetworkException())
        ),
        ExceptionTestCase(
            name = "Generic RuntimeException during API call",
            exception = RuntimeException("Something went wrong"),
            expectedResource = Resource.Exception(RuntimeException("Something went wrong"))
        ),
        ExceptionTestCase(
            name = "IllegalStateException during API call",
            exception = IllegalStateException("Invalid state"),
            expectedResource = Resource.Exception(IllegalStateException("Invalid state"))
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            runTest {
                coEvery { apiService.getAppointments() } throws testCase.exception

                repository.getAppointments().test {
                    assertEquals(Resource.Loading, awaitItem())
                    
                    val result = awaitItem()
                    when (testCase.expectedResource) {
                        is Resource.Error -> {
                            assertTrue(result is Resource.Error)
                            assertEquals(testCase.expectedResource.code, result.code)
                        }
                        is Resource.Exception -> {
                            assertTrue(result is Resource.Exception)
                            assertEquals(
                                testCase.expectedResource.exception::class,
                                result.exception::class
                            )
                        }
                        is Resource.Loading -> {
                            // Should not happen in this test
                        }
                        is Resource.Success -> {
                            // Should not happen in this test
                        }
                    }
                    
                    awaitComplete()
                }
            }
        }
    }

    @Test
    fun `getAppointments includes appointments with parsing errors using fallback values`() = runTest {
        val appointmentResponses = listOf(
            createValidAppointmentResponse("1"),
            createAppointmentResponseWithInvalidDate("2"),
            createValidAppointmentResponse("3")
        )
        val response = Response.success(AppointmentsResponse(appointmentResponses))
        
        coEvery { apiService.getAppointments() } returns response

        repository.getAppointments().test {
            assertEquals(Resource.Loading, awaitItem())
            
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            // Should contain all appointments - invalid dates use current time as fallback
            assertEquals(3, result.data.size)
            assertEquals("1", result.data[0].id)
            assertEquals("2", result.data[1].id)
            assertEquals("3", result.data[2].id)
            
            awaitComplete()
        }
    }

    private fun createValidAppointmentResponse(id: String) = AppointmentResponse(
        appointment_id = id,
        patient_id = "patient_$id",
        provider_id = "provider_$id",
        status = "Scheduled",
        appointment_type = "Consultation",
        start = "2024-01-15T10:00:00Z",
        end = "2024-01-15T11:00:00Z",
        duration_in_minutes = 60,
        recurrence_type = "none"
    )

    private fun createInvalidAppointmentResponse() = AppointmentResponse(
        appointment_id = null, // Invalid - null ID
        patient_id = "patient_invalid",
        provider_id = "provider_invalid",
        status = "Scheduled",
        appointment_type = "Consultation",
        start = "2024-01-15T10:00:00Z",
        end = "2024-01-15T11:00:00Z",
        duration_in_minutes = 60,
        recurrence_type = "none"
    )

    private fun createAppointmentResponseWithInvalidDate(id: String) = AppointmentResponse(
        appointment_id = id,
        patient_id = "patient_$id",
        provider_id = "provider_$id",
        status = "Scheduled",
        appointment_type = "Consultation",
        start = "invalid-date-format", // Invalid date
        end = "2024-01-15T11:00:00Z",
        duration_in_minutes = 60,
        recurrence_type = "none"
    )

    private data class ErrorTestCase(
        val name: String,
        val response: Response<AppointmentsResponse>,
        val expectedCode: Int,
        val expectedMessage: String
    )

    private data class ExceptionTestCase(
        val name: String,
        val exception: Exception,
        val expectedResource: Resource<List<Appointment>>
    )
}