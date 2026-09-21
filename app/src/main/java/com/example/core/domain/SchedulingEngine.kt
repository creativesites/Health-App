package com.example.core.domain

import com.example.core.model.Appointment
import com.example.core.model.AppointmentStatus
import com.example.core.model.AvailabilityException
import com.example.core.model.SpecialistAvailabilityRule
import com.example.core.model.TimeSlot
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object SchedulingEngine {

    /**
     * Mathematically generates all available bookable slots for a practitioner on a specific date.
     * availableSlots = schedule + exceptions - appointments - buffers
     */
    fun generateSlots(
        practitionerId: String,
        targetDate: LocalDate,
        rules: List<SpecialistAvailabilityRule>,
        exceptions: List<AvailabilityException>,
        appointments: List<Appointment>,
        serviceDurationMinutes: Int = 30
    ): List<TimeSlot> {
        val dayOfWeekStr = targetDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }

        // 1. Check if the entire day is blocked by an exception
        val dayExceptions = exceptions.filter {
            it.practitionerId == practitionerId &&
            LocalDate.parse(it.dateIso) == targetDate &&
            it.enabled
        }

        val isWholeDayBlocked = dayExceptions.any { it.type == "BLOCKED" && it.startTime == null }
        if (isWholeDayBlocked) {
            return emptyList()
        }

        // 2. Fetch standard rules for this day of the week
        val activeRules = rules.filter {
            it.practitionerId == practitionerId &&
            it.dayOfWeek.equals(dayOfWeekStr, ignoreCase = true) &&
            it.enabled
        }

        if (activeRules.isEmpty()) {
            return emptyList()
        }

        val generatedSlots = mutableListOf<TimeSlot>()

        // 3. For each active rule, generate potential intervals
        for (rule in activeRules) {
            val ruleStart = LocalTime.parse(rule.startTime)
            val ruleEnd = LocalTime.parse(rule.endTime)
            val slotDuration = rule.slotDurationMinutes
            val buffer = rule.bufferMinutes

            // Use the larger of the rule's slot duration OR the requested service duration
            val durationToUse = maxOf(slotDuration, serviceDurationMinutes)

            var currentStart = ruleStart
            while (currentStart.plusMinutes(durationToUse.toLong()).isBefore(ruleEnd) || 
                   currentStart.plusMinutes(durationToUse.toLong()) == ruleEnd) {
                
                val currentEnd = currentStart.plusMinutes(durationToUse.toLong())
                
                // Form slot label
                val timeLabel = "${currentStart.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${currentEnd.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                
                val candidateSlot = TimeSlot(
                    id = "slot_${practitionerId}_${targetDate}_${currentStart}",
                    practitionerId = practitionerId,
                    dateIso = targetDate.toString(),
                    timeLabel = timeLabel,
                    isAvailable = true
                )
                
                generatedSlots.add(candidateSlot)
                
                // Next candidate slot starts after the duration plus buffer
                currentStart = currentStart.plusMinutes((durationToUse + buffer).toLong())
            }
        }

        // 4. Filter out slots that overlap with partial exceptions (blocked windows)
        val filteredByExceptions = generatedSlots.filter { slot ->
            val slotRange = parseSlotRange(slot.timeLabel) ?: return@filter false
            
            val isBlocked = dayExceptions.any { exc ->
                if (exc.type == "BLOCKED" && exc.startTime != null && exc.endTime != null) {
                    val excStart = LocalTime.parse(exc.startTime)
                    val excEnd = LocalTime.parse(exc.endTime)
                    // Overlap check: max(slotStart, excStart) < min(slotEnd, excEnd)
                    val maxStart = if (slotRange.first.isAfter(excStart)) slotRange.first else excStart
                    val minEnd = if (slotRange.second.isBefore(excEnd)) slotRange.second else excEnd
                    maxStart.isBefore(minEnd)
                } else false
            }
            !isBlocked
        }

        // 5. Filter out slots that overlap with existing bookings (considering buffers)
        val activeAppointments = appointments.filter {
            it.practitionerId == practitionerId &&
            it.dateIso == targetDate.toString() &&
            it.status != AppointmentStatus.CANCELLED &&
            it.status != AppointmentStatus.REFUNDED
        }

        val filteredByAppointments = filteredByExceptions.filter { slot ->
            val slotRange = parseSlotRange(slot.timeLabel) ?: return@filter false
            
            val overlapsAppointment = activeAppointments.any { apt ->
                val aptRange = parseSlotRange(apt.timeSlotLabel) ?: return@any false
                
                // Fetch the buffer configured for this day/practitioner
                val matchingRule = activeRules.firstOrNull()
                val buffer = matchingRule?.bufferMinutes ?: 10
                
                // Create appointment interval with buffer included to avoid overlaps
                // Buffer is added after the appointment ends.
                val aptStart = aptRange.first
                val aptEndWithBuffer = aptRange.second.plusMinutes(buffer.toLong())
                
                // Overlap: max(slotStart, aptStart) < min(slotEnd, aptEndWithBuffer)
                val maxStart = if (slotRange.first.isAfter(aptStart)) slotRange.first else aptStart
                val minEnd = if (slotRange.second.isBefore(aptEndWithBuffer)) slotRange.second else aptEndWithBuffer
                maxStart.isBefore(minEnd)
            }
            !overlapsAppointment
        }

        return filteredByAppointments.sortedBy { parseSlotRange(it.timeLabel)?.first }
    }

    private fun parseSlotRange(timeLabel: String): Pair<LocalTime, LocalTime>? {
        return try {
            val parts = timeLabel.split("-").map { it.trim() }
            if (parts.size == 2) {
                // Support both "09:00" and "09:30 AM" or similar formats
                val cleanPart1 = parts[0].substringBefore(" ")
                val cleanPart2 = parts[1].substringBefore(" ")
                Pair(LocalTime.parse(cleanPart1), LocalTime.parse(cleanPart2))
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
