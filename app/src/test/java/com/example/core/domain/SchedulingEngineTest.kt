package com.example.core.domain

import com.example.core.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class SchedulingEngineTest {

    private val doctorId1 = "doc_chileshe_01"
    private val doctorId2 = "doc_mwansa_02"

    private val standardMondayRule = SpecialistAvailabilityRule(
        id = "rule_mon",
        practitionerId = doctorId1,
        dayOfWeek = "Monday",
        startTime = "09:00",
        endTime = "12:00",
        slotDurationMinutes = 30,
        bufferMinutes = 0,
        enabled = true
    )

    private val mondayWithBufferRule = SpecialistAvailabilityRule(
        id = "rule_mon_buffer",
        practitionerId = doctorId1,
        dayOfWeek = "Monday",
        startTime = "09:00",
        endTime = "12:00",
        slotDurationMinutes = 30,
        bufferMinutes = 10,
        enabled = true
    )

    @Test
    fun `Test 1 - Monday standard availability with 30-min slots`() {
        val targetDate = LocalDate.of(2026, 9, 21) // A Monday
        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule),
            exceptions = emptyList(),
            appointments = emptyList()
        )

        val expectedSlotTimes = listOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30")
        assertEquals(expectedSlotTimes.size, slots.size)
        expectedSlotTimes.forEachIndexed { index, time ->
            assertTrue(slots[index].timeLabel.startsWith(time))
        }
    }

    @Test
    fun `Test 2 - Monday availability with 10-00 slot already booked`() {
        val targetDate = LocalDate.of(2026, 9, 21) // Monday
        val appointments = listOf(
            Appointment(
                id = "apt_1",
                patientId = "pat_1",
                practitionerId = doctorId1,
                practitionerName = "Mutale Chileshe",
                practitionerTitle = "Psychologist",
                specialty = SpecialtyCategory.MENTAL_HEALTH,
                serviceId = "srv_1",
                serviceName = "Intake",
                dateIso = "2026-09-21",
                timeSlotLabel = "10:00 - 10:30",
                consultationType = ConsultationType.ONLINE,
                locationDescription = "Online",
                priceZmw = 400.0,
                status = AppointmentStatus.CONFIRMED,
                paymentStatus = PaymentStatus.PAID
            )
        )

        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule),
            exceptions = emptyList(),
            appointments = appointments
        )

        // The "10:00 - 10:30" slot must be filtered out
        assertFalse(slots.any { it.timeLabel.startsWith("10:00") })
        assertEquals(5, slots.size)
    }

    @Test
    fun `Test 3 - Availability with 45-min duration on short window`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        val shortRule = SpecialistAvailabilityRule(
            id = "rule_short",
            practitionerId = doctorId1,
            dayOfWeek = "Monday",
            startTime = "09:00",
            endTime = "10:00",
            slotDurationMinutes = 45,
            bufferMinutes = 0,
            enabled = true
        )

        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(shortRule),
            exceptions = emptyList(),
            appointments = emptyList(),
            serviceDurationMinutes = 45
        )

        // Only 09:00-09:45 should fit. 09:45-10:30 exceeds availability.
        assertEquals(1, slots.size)
        assertTrue(slots[0].timeLabel.startsWith("09:00"))
    }

    @Test
    fun `Test 4 - Buffer time restricts subsequent slots`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(mondayWithBufferRule),
            exceptions = emptyList(),
            appointments = emptyList()
        )

        // 09:00 - 09:30, buffer 10m -> 09:40 - 10:10, buffer 10m -> 10:20 - 10:50, buffer 10m -> 11:00 - 11:30
        // 11:30 ends, next starts at 11:40 -> ends at 12:10 which exceeds 12:00.
        // Expected slot starts: 09:00, 09:40, 10:20, 11:00
        val expectedStarts = listOf("09:00", "09:40", "10:20", "11:00")
        assertEquals(expectedStarts.size, slots.size)
        expectedStarts.forEachIndexed { index, start ->
            assertTrue(slots[index].timeLabel.startsWith(start))
        }
    }

    @Test
    fun `Test 5 - Blocked exceptions cancel slot availability`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        val exceptions = listOf(
            AvailabilityException(
                id = "exc_1",
                practitionerId = doctorId1,
                dateIso = "2026-09-21",
                startTime = "10:15",
                endTime = "11:15",
                type = "BLOCKED",
                reason = "Meeting",
                enabled = true
            )
        )

        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule),
            exceptions = exceptions,
            appointments = emptyList()
        )

        // Standard slots: 09:00, 09:30, 10:00, 10:30, 11:00, 11:30
        // 10:15-11:15 overlaps with:
        // - "10:00 - 10:30" (overlap range 10:15 - 10:30) -> blocked!
        // - "10:30 - 11:00" (overlap range 10:30 - 11:00) -> blocked!
        // - "11:00 - 11:30" (overlap range 11:00 - 11:15) -> blocked!
        // Surviving slots: 09:00, 09:30, 11:30
        val expectedSlots = listOf("09:00", "09:30", "11:30")
        assertEquals(expectedSlots.size, slots.size)
        expectedSlots.forEachIndexed { index, start ->
            assertTrue(slots[index].timeLabel.startsWith(start))
        }
    }

    @Test
    fun `Test 6 - Weekend or non-working days have zero availability`() {
        val targetDate = LocalDate.of(2026, 9, 20) // A Sunday
        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule), // Only active on Monday
            exceptions = emptyList(),
            appointments = emptyList()
        )
        assertTrue(slots.isEmpty())
    }

    @Test
    fun `Test 7 - Fully booked day has zero slots`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        // Let's create appointments blocking every standard slot
        val times = listOf("09:00 - 09:30", "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00", "11:00 - 11:30", "11:30 - 12:00")
        val appointments = times.mapIndexed { idx, time ->
            Appointment(
                id = "apt_$idx",
                patientId = "pat_1",
                practitionerId = doctorId1,
                practitionerName = "Mutale",
                practitionerTitle = "Doc",
                specialty = SpecialtyCategory.MENTAL_HEALTH,
                serviceId = "srv_1",
                serviceName = "Service",
                dateIso = "2026-09-21",
                timeSlotLabel = time,
                consultationType = ConsultationType.ONLINE,
                locationDescription = "Online",
                priceZmw = 400.0,
                status = AppointmentStatus.CONFIRMED,
                paymentStatus = PaymentStatus.PAID
            )
        }

        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule),
            exceptions = emptyList(),
            appointments = appointments
        )
        assertTrue(slots.isEmpty())
    }

    @Test
    fun `Test 8 - Cancelled appointments release their slots`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        val appointments = listOf(
            Appointment(
                id = "apt_1",
                patientId = "pat_1",
                practitionerId = doctorId1,
                practitionerName = "Mutale",
                practitionerTitle = "Doc",
                specialty = SpecialtyCategory.MENTAL_HEALTH,
                serviceId = "srv_1",
                serviceName = "Service",
                dateIso = "2026-09-21",
                timeSlotLabel = "10:00 - 10:30",
                consultationType = ConsultationType.ONLINE,
                locationDescription = "Online",
                priceZmw = 400.0,
                status = AppointmentStatus.CANCELLED, // CANCELLED status
                paymentStatus = PaymentStatus.PENDING
            )
        )

        val slots = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = listOf(standardMondayRule),
            exceptions = emptyList(),
            appointments = appointments
        )

        // Slot "10:00 - 10:30" should be available because the appointment is cancelled
        assertTrue(slots.any { it.timeLabel.startsWith("10:00") })
        assertEquals(6, slots.size)
    }

    @Test
    fun `Test 9 - Multiple specialist schedules remain completely isolated`() {
        val targetDate = LocalDate.of(2026, 9, 21)
        val chilesheRule = SpecialistAvailabilityRule(
            id = "rule_chileshe",
            practitionerId = doctorId1,
            dayOfWeek = "Monday",
            startTime = "09:00",
            endTime = "11:00",
            slotDurationMinutes = 30,
            bufferMinutes = 0,
            enabled = true
        )
        val mwansaRule = SpecialistAvailabilityRule(
            id = "rule_mwansa",
            practitionerId = doctorId2,
            dayOfWeek = "Monday",
            startTime = "14:00",
            endTime = "16:00",
            slotDurationMinutes = 60,
            bufferMinutes = 0,
            enabled = true
        )

        val rules = listOf(chilesheRule, mwansaRule)

        // Chileshe slots (Monday morning)
        val slotsChileshe = SchedulingEngine.generateSlots(
            practitionerId = doctorId1,
            targetDate = targetDate,
            rules = rules,
            exceptions = emptyList(),
            appointments = emptyList()
        )
        assertEquals(4, slotsChileshe.size)
        assertTrue(slotsChileshe.first().timeLabel.startsWith("09:00"))

        // Mwansa slots (Monday afternoon)
        val slotsMwansa = SchedulingEngine.generateSlots(
            practitionerId = doctorId2,
            targetDate = targetDate,
            rules = rules,
            exceptions = emptyList(),
            appointments = emptyList()
        )
        assertEquals(2, slotsMwansa.size)
        assertTrue(slotsMwansa.first().timeLabel.startsWith("14:00"))
    }
}
