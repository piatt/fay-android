package com.example.fay.appointments.domain

import com.example.fay.appointments.data.api.Appointment
import com.example.fay.appointments.data.api.AppointmentStatus
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFilteredAppointmentsUseCaseTest {

    @Test
    fun `empty appointments list returns empty result`() {
        val result = getFilteredAppointmentsUseCase(emptyList(), AppointmentStatus.UPCOMING)
        
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filter upcoming appointments only returns upcoming status`() {
        val appointments = listOf(
            createTestAppointment("1", AppointmentStatus.UPCOMING),
            createTestAppointment("2", AppointmentStatus.PAST),
            createTestAppointment("3", AppointmentStatus.UPCOMING),
            createTestAppointment("4", AppointmentStatus.UNKNOWN)
        )
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.UPCOMING)
        
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("3", result[1].id)
    }

    @Test
    fun `filter past appointments only returns past status`() {
        val appointments = listOf(
            createTestAppointment("1", AppointmentStatus.UPCOMING),
            createTestAppointment("2", AppointmentStatus.PAST),
            createTestAppointment("3", AppointmentStatus.PAST),
            createTestAppointment("4", AppointmentStatus.UNKNOWN)
        )
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.PAST)
        
        assertEquals(2, result.size)
        assertEquals("2", result[0].id)
        assertEquals("3", result[1].id)
    }

    @Test
    fun `filter unknown appointments only returns unknown status`() {
        val appointments = listOf(
            createTestAppointment("1", AppointmentStatus.UPCOMING),
            createTestAppointment("2", AppointmentStatus.PAST),
            createTestAppointment("3", AppointmentStatus.UNKNOWN),
            createTestAppointment("4", AppointmentStatus.UNKNOWN)
        )
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.UNKNOWN)
        
        assertEquals(2, result.size)
        assertEquals("3", result[0].id)
        assertEquals("4", result[1].id)
    }

    @Test
    fun `no matching status returns empty result`() {
        val appointments = listOf(
            createTestAppointment("1", AppointmentStatus.UPCOMING),
            createTestAppointment("2", AppointmentStatus.UPCOMING)
        )
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.PAST)
        
        assertTrue(result.isEmpty())
    }

    @Test
    fun `single appointment matching status returns single result`() {
        val appointments = listOf(
            createTestAppointment("1", AppointmentStatus.UPCOMING)
        )
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.UPCOMING)
        
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @TestFactory
    fun `appointment state formatting scenarios`() = listOf(
        FormattingTestCase(
            name = "morning appointment formatting",
            startHour = 9,
            startMinute = 0,
            endHour = 10,
            endMinute = 30,
            appointmentType = "Consultation",
            providerName = "Dr. Smith",
            expectedDescriptionContains = listOf("Consultation", "Dr. Smith")
        ),
        FormattingTestCase(
            name = "afternoon appointment formatting", 
            startHour = 14,
            startMinute = 15,
            endHour = 15,
            endMinute = 45,
            appointmentType = "Follow-up",
            providerName = "Jane Williams, RD",
            expectedDescriptionContains = listOf("Follow-up", "Jane Williams, RD")
        ),
        FormattingTestCase(
            name = "evening appointment formatting",
            startHour = 18,
            startMinute = 30,
            endHour = 19,
            endMinute = 30,
            appointmentType = "Initial Assessment",
            providerName = "Dr. Johnson",
            expectedDescriptionContains = listOf("Initial Assessment", "Dr. Johnson")
        )
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            val appointment = createTestAppointmentWithTime(
                id = "test",
                status = AppointmentStatus.UPCOMING,
                startHour = testCase.startHour,
                startMinute = testCase.startMinute,
                endHour = testCase.endHour,
                endMinute = testCase.endMinute,
                type = testCase.appointmentType,
                providerName = testCase.providerName
            )
            
            val result = getFilteredAppointmentsUseCase(listOf(appointment), AppointmentStatus.UPCOMING)
            
            assertEquals(1, result.size)
            val appointmentState = result[0]
            
            // Verify description contains expected parts
            testCase.expectedDescriptionContains.forEach { expectedPart ->
                assertTrue(
                    appointmentState.description.contains(expectedPart),
                    "Description '${appointmentState.description}' should contain '$expectedPart'"
                )
            }
            
            // Verify time range formatting (should be in 12-hour format with AM/PM and timezone)
            assertTrue(appointmentState.formattedTimeRange.contains(":"))
            assertTrue(
                appointmentState.formattedTimeRange.contains("AM") || 
                appointmentState.formattedTimeRange.contains("PM")
            )
            assertTrue(appointmentState.formattedTimeRange.contains("("))
            assertTrue(appointmentState.formattedTimeRange.contains(")"))
            
            // Verify month formatting (should be uppercase abbreviated)
            assertTrue(appointmentState.formattedMonth.all { it.isUpperCase() })
            assertEquals(3, appointmentState.formattedMonth.length)
            
            // Verify day formatting (should be numeric string)
            assertTrue(appointmentState.formattedDay.all { it.isDigit() })
        }
    }

    @TestFactory
    fun `appointment state date formatting across months`() = listOf(
        DateTestCase("January appointment", 1, 15, "JAN"),
        DateTestCase("February appointment", 2, 28, "FEB"),
        DateTestCase("March appointment", 3, 10, "MAR"),
        DateTestCase("April appointment", 4, 5, "APR"),
        DateTestCase("May appointment", 5, 20, "MAY"),
        DateTestCase("June appointment", 6, 30, "JUN"),
        DateTestCase("July appointment", 7, 4, "JUL"),
        DateTestCase("August appointment", 8, 15, "AUG"),
        DateTestCase("September appointment", 9, 22, "SEP"),
        DateTestCase("October appointment", 10, 31, "OCT"),
        DateTestCase("November appointment", 11, 11, "NOV"),
        DateTestCase("December appointment", 12, 25, "DEC")
    ).map { testCase ->
        DynamicTest.dynamicTest(testCase.name) {
            val appointment = createTestAppointmentWithDate(
                id = "test",
                status = AppointmentStatus.UPCOMING,
                month = testCase.month,
                day = testCase.day
            )
            
            val result = getFilteredAppointmentsUseCase(listOf(appointment), AppointmentStatus.UPCOMING)
            
            assertEquals(1, result.size)
            val appointmentState = result[0]
            
            assertEquals(testCase.expectedMonth, appointmentState.formattedMonth)
            assertEquals(testCase.day.toString(), appointmentState.formattedDay)
        }
    }

    @Test
    fun `maintains original appointment order after filtering`() {
        val appointments = listOf(
            createTestAppointment("first", AppointmentStatus.UPCOMING),
            createTestAppointment("second", AppointmentStatus.PAST),
            createTestAppointment("third", AppointmentStatus.UPCOMING),
            createTestAppointment("fourth", AppointmentStatus.PAST),
            createTestAppointment("fifth", AppointmentStatus.UPCOMING)
        )
        
        val upcomingResult = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.UPCOMING)
        val pastResult = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.PAST)
        
        // Upcoming appointments should maintain order: first, third, fifth
        assertEquals(3, upcomingResult.size)
        assertEquals("first", upcomingResult[0].id)
        assertEquals("third", upcomingResult[1].id)
        assertEquals("fifth", upcomingResult[2].id)
        
        // Past appointments should maintain order: second, fourth
        assertEquals(2, pastResult.size)
        assertEquals("second", pastResult[0].id)
        assertEquals("fourth", pastResult[1].id)
    }

    @Test
    fun `large appointment list performance`() {
        // Create a large list of appointments
        val appointments = (1..1000).map { index ->
            val status = when (index % 3) {
                0 -> AppointmentStatus.UPCOMING
                1 -> AppointmentStatus.PAST
                else -> AppointmentStatus.UNKNOWN
            }
            createTestAppointment(index.toString(), status)
        }
        
        val result = getFilteredAppointmentsUseCase(appointments, AppointmentStatus.UPCOMING)
        
        // Should return approximately 1/3 of appointments (those with index % 3 == 0)
        val expectedCount = appointments.count { it.status == AppointmentStatus.UPCOMING }
        assertEquals(expectedCount, result.size)
        
        // Verify all results have correct status
        assertTrue(result.all { appointmentState ->
            appointments.find { it.id == appointmentState.id }?.status == AppointmentStatus.UPCOMING
        })
    }

    private fun createTestAppointment(id: String, status: AppointmentStatus): Appointment {
        val startTime = ZonedDateTime.of(2024, 1, 15, 10, 0, 0, 0, ZoneId.systemDefault())
        val endTime = startTime.plusHours(1)
        
        return Appointment(
            id = id,
            patientId = "patient_$id",
            providerId = "provider_$id",
            providerName = "Jane Williams, RD",
            status = status,
            type = "Consultation",
            recurrenceType = "none",
            start = startTime,
            end = endTime,
            duration = 60
        )
    }

    private fun createTestAppointmentWithTime(
        id: String,
        status: AppointmentStatus,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        type: String,
        providerName: String
    ): Appointment {
        val startTime = ZonedDateTime.of(2024, 1, 15, startHour, startMinute, 0, 0, ZoneId.systemDefault())
        val endTime = ZonedDateTime.of(2024, 1, 15, endHour, endMinute, 0, 0, ZoneId.systemDefault())
        
        return Appointment(
            id = id,
            patientId = "patient_$id",
            providerId = "provider_$id",
            providerName = providerName,
            status = status,
            type = type,
            recurrenceType = "none",
            start = startTime,
            end = endTime,
            duration = ((endTime.toEpochSecond() - startTime.toEpochSecond()) / 60).toInt()
        )
    }

    private fun createTestAppointmentWithDate(
        id: String,
        status: AppointmentStatus,
        month: Int,
        day: Int
    ): Appointment {
        val startTime = ZonedDateTime.of(2024, month, day, 10, 0, 0, 0, ZoneId.systemDefault())
        val endTime = startTime.plusHours(1)
        
        return Appointment(
            id = id,
            patientId = "patient_$id",
            providerId = "provider_$id",
            providerName = "Jane Williams, RD",
            status = status,
            type = "Consultation",
            recurrenceType = "none",
            start = startTime,
            end = endTime,
            duration = 60
        )
    }

    private data class FormattingTestCase(
        val name: String,
        val startHour: Int,
        val startMinute: Int,
        val endHour: Int,
        val endMinute: Int,
        val appointmentType: String,
        val providerName: String,
        val expectedDescriptionContains: List<String>
    )

    private data class DateTestCase(
        val name: String,
        val month: Int,
        val day: Int,
        val expectedMonth: String
    )
}