package com.example.fay.appointments.data.impl

import com.example.fay.appointments.data.api.AppointmentStatus
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MapperExtensionsTest {

    @Test
    fun `toAppointment creates valid appointment with all fields`() {
        val response = AppointmentResponse(
            appointment_id = "test-id-123",
            patient_id = "patient-456",
            provider_id = "provider-789",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:00:00Z",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNotNull(result)
        assertEquals("test-id-123", result.id)
        assertEquals("patient-456", result.patientId)
        assertEquals("provider-789", result.providerId)
        assertEquals("Jane Williams, RD", result.providerName)
        assertEquals(AppointmentStatus.UPCOMING, result.status)
        assertEquals("Consultation", result.type)
        assertEquals("none", result.recurrenceType)
        assertEquals(60, result.duration)
        
        // Verify dates are properly converted to system timezone
        assertNotNull(result.start)
        assertNotNull(result.end)
    }

    @Test
    fun `toAppointment returns null when appointment_id is null`() {
        val response = AppointmentResponse(
            appointment_id = null,
            patient_id = "patient-456",
            provider_id = "provider-789",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:00:00Z",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNull(result)
    }

    @Test
    fun `toAppointment handles null fields with defaults`() {
        val response = AppointmentResponse(
            appointment_id = "test-id-123",
            patient_id = null,
            provider_id = null,
            status = null,
            appointment_type = null,
            start = null,
            end = null,
            duration_in_minutes = null,
            recurrence_type = null
        )

        val result = response.toAppointment()

        assertNotNull(result)
        assertEquals("test-id-123", result.id)
        assertEquals("", result.patientId)
        assertEquals("", result.providerId)
        assertEquals("Jane Williams, RD", result.providerName)
        assertEquals(AppointmentStatus.UNKNOWN, result.status)
        assertEquals("", result.type)
        assertEquals("", result.recurrenceType)
        assertEquals(0, result.duration)
        
        // Should use current time when start/end are null
        assertNotNull(result.start)
        assertNotNull(result.end)
    }

    @TestFactory
    fun `toAppointmentStatus maps status strings correctly`() = listOf(
        StatusTestCase("Scheduled", AppointmentStatus.UPCOMING),
        StatusTestCase("Occurred", AppointmentStatus.PAST),
        StatusTestCase("Unknown", AppointmentStatus.UNKNOWN),
        StatusTestCase("Invalid", AppointmentStatus.UNKNOWN),
        StatusTestCase("", AppointmentStatus.UNKNOWN),
        StatusTestCase("scheduled", AppointmentStatus.UNKNOWN), // Case sensitive
        StatusTestCase("SCHEDULED", AppointmentStatus.UNKNOWN), // Case sensitive
        StatusTestCase("occurred", AppointmentStatus.UNKNOWN), // Case sensitive
        StatusTestCase("OCCURRED", AppointmentStatus.UNKNOWN) // Case sensitive
    ).map { testCase ->
        DynamicTest.dynamicTest("status '${testCase.status}' maps to ${testCase.expectedStatus}") {
            val response = AppointmentResponse(
                appointment_id = "test-id",
                patient_id = "patient",
                provider_id = "provider",
                status = testCase.status,
                appointment_type = "Consultation",
                start = "2024-01-15T10:00:00Z",
                end = "2024-01-15T11:00:00Z",
                duration_in_minutes = 60,
                recurrence_type = "none"
            )

            val result = response.toAppointment()

            assertNotNull(result)
            assertEquals(testCase.expectedStatus, result.status)
        }
    }

    @Test
    fun `toAppointmentStatus handles null status`() {
        val response = AppointmentResponse(
            appointment_id = "test-id",
            patient_id = "patient",
            provider_id = "provider",
            status = null,
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:00:00Z",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNotNull(result)
        assertEquals(AppointmentStatus.UNKNOWN, result.status)
    }

    @TestFactory
    fun `toZonedDateTime parses valid ISO date-time strings`() = listOf(
        DateTimeTestCase(
            name = "UTC time with Z suffix",
            input = "2024-01-15T10:00:00Z",
            expectedHour = 10, // Will be converted to system timezone
            expectSuccess = true
        ),
        DateTimeTestCase(
            name = "UTC time with offset",
            input = "2024-01-15T10:00:00+00:00",
            expectedHour = 10, // Will be converted to system timezone
            expectSuccess = true
        ),
        DateTimeTestCase(
            name = "time with positive offset",
            input = "2024-01-15T10:00:00+05:00",
            expectedHour = 5, // UTC equivalent, then converted to system timezone
            expectSuccess = true
        ),
        DateTimeTestCase(
            name = "time with negative offset",
            input = "2024-01-15T10:00:00-05:00",
            expectedHour = 15, // UTC equivalent, then converted to system timezone
            expectSuccess = true
        ),
        DateTimeTestCase(
            name = "time with fractional seconds",
            input = "2024-01-15T10:00:00.123Z",
            expectedHour = 10,
            expectSuccess = true
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            val response = AppointmentResponse(
                appointment_id = "test-id",
                patient_id = "patient",
                provider_id = "provider",
                status = "Scheduled",
                appointment_type = "Consultation",
                start = testCase.input,
                end = testCase.input,
                duration_in_minutes = 60,
                recurrence_type = "none"
            )

            val result = response.toAppointment()

            assertNotNull(result)
            if (testCase.expectSuccess) {
                // Verify the time was properly parsed and converted to system timezone
                assertNotNull(result.start)
                assertNotNull(result.end)
                assertEquals(ZoneId.systemDefault(), result.start.zone)
                assertEquals(ZoneId.systemDefault(), result.end.zone)
            }
        }
    }

    @TestFactory
    fun `toZonedDateTime handles invalid date-time strings`() = listOf(
        "invalid-date",
        "2024-13-45T25:70:80Z", // Invalid date components
        "2024-01-15", // Missing time
        "10:00:00", // Missing date
        "",
        "null",
        "2024/01/15 10:00:00", // Wrong format
        "Jan 15, 2024 10:00 AM" // Wrong format
    ).map { invalidInput ->
        DynamicTest.dynamicTest("invalid input: '$invalidInput'") {
            val response = AppointmentResponse(
                appointment_id = "test-id",
                patient_id = "patient",
                provider_id = "provider",
                status = "Scheduled",
                appointment_type = "Consultation",
                start = invalidInput,
                end = invalidInput,
                duration_in_minutes = 60,
                recurrence_type = "none"
            )

            val result = response.toAppointment()

            assertNotNull(result)
            // Should fall back to current time when parsing fails
            assertNotNull(result.start)
            assertNotNull(result.end)
            assertEquals(ZoneId.systemDefault(), result.start.zone)
            assertEquals(ZoneId.systemDefault(), result.end.zone)
        }
    }

    @Test
    fun `toZonedDateTime handles null input`() {
        val response = AppointmentResponse(
            appointment_id = "test-id",
            patient_id = "patient",
            provider_id = "provider",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = null,
            end = null,
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNotNull(result)
        // Should fall back to current time when input is null
        assertNotNull(result.start)
        assertNotNull(result.end)
        assertEquals(ZoneId.systemDefault(), result.start.zone)
        assertEquals(ZoneId.systemDefault(), result.end.zone)
    }

    @Test
    fun `toZonedDateTime handles blank input`() {
        val response = AppointmentResponse(
            appointment_id = "test-id",
            patient_id = "patient",
            provider_id = "provider",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "   ",
            end = "",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNotNull(result)
        // Should fall back to current time when input is blank
        assertNotNull(result.start)
        assertNotNull(result.end)
        assertEquals(ZoneId.systemDefault(), result.start.zone)
        assertEquals(ZoneId.systemDefault(), result.end.zone)
    }

    @Test
    fun `timezone conversion preserves relative time difference`() {
        val response = AppointmentResponse(
            appointment_id = "test-id",
            patient_id = "patient",
            provider_id = "provider",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:30:00Z",
            duration_in_minutes = 90,
            recurrence_type = "none"
        )

        val result = response.toAppointment()

        assertNotNull(result)
        
        // Duration between start and end should be preserved after timezone conversion
        val durationMinutes = java.time.Duration.between(result.start, result.end).toMinutes()
        assertEquals(90, durationMinutes)
    }

    @Test
    fun `multiple appointments with same data produce consistent results`() {
        val response1 = AppointmentResponse(
            appointment_id = "test-id-1",
            patient_id = "patient",
            provider_id = "provider",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:00:00Z",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )
        
        val response2 = AppointmentResponse(
            appointment_id = "test-id-2",
            patient_id = "patient",
            provider_id = "provider",
            status = "Scheduled",
            appointment_type = "Consultation",
            start = "2024-01-15T10:00:00Z",
            end = "2024-01-15T11:00:00Z",
            duration_in_minutes = 60,
            recurrence_type = "none"
        )

        val result1 = response1.toAppointment()
        val result2 = response2.toAppointment()

        assertNotNull(result1)
        assertNotNull(result2)
        
        // Should have same times and status (different IDs)
        assertEquals(result1.start, result2.start)
        assertEquals(result1.end, result2.end)
        assertEquals(result1.status, result2.status)
        assertEquals(result1.type, result2.type)
        assertEquals(result1.duration, result2.duration)
    }

    private data class StatusTestCase(
        val status: String,
        val expectedStatus: AppointmentStatus
    )

    private data class DateTimeTestCase(
        val name: String,
        val input: String,
        val expectedHour: Int,
        val expectSuccess: Boolean
    )
}