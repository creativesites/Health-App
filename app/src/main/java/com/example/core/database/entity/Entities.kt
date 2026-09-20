package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.CareGoal
import com.example.core.model.JournalEntry
import com.example.core.model.MoodCheckIn
import com.example.core.model.Patient

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: String = "pat_demo_me",
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val selectedCity: String,
    val preferredLanguage: String = "English",
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
    val avatarUri: String? = null,
    val avatarPresetId: String? = null
) {
    fun toDomain(): Patient = Patient(
        id = id,
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        selectedCity = selectedCity,
        preferredLanguage = preferredLanguage,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        avatarUri = avatarUri,
        avatarPresetId = avatarPresetId
    )

    companion object {
        fun fromDomain(patient: Patient): PatientEntity = PatientEntity(
            id = patient.id,
            fullName = patient.fullName,
            email = patient.email,
            phoneNumber = patient.phoneNumber,
            selectedCity = patient.selectedCity,
            preferredLanguage = patient.preferredLanguage,
            emergencyContactName = patient.emergencyContactName,
            emergencyContactPhone = patient.emergencyContactPhone,
            avatarUri = patient.avatarUri,
            avatarPresetId = patient.avatarPresetId
        )
    }
}

@Entity(tableName = "care_goals")
data class CareGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val progressPercent: Int,
    val targetDescription: String,
    val isCompleted: Boolean
) {
    fun toDomain(): CareGoal = CareGoal(
        id = id,
        title = title,
        category = category,
        progressPercent = progressPercent,
        targetDescription = targetDescription,
        isCompleted = isCompleted
    )

    companion object {
        fun fromDomain(goal: CareGoal): CareGoalEntity = CareGoalEntity(
            id = goal.id,
            title = goal.title,
            category = goal.category,
            progressPercent = goal.progressPercent,
            targetDescription = goal.targetDescription,
            isCompleted = goal.isCompleted
        )
    }
}

@Entity(tableName = "mood_checkins")
data class MoodCheckInEntity(
    @PrimaryKey val id: String,
    val moodValue: Int,
    val moodLabel: String,
    val feelingsCsv: String,
    val note: String?,
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): MoodCheckIn = MoodCheckIn(
        id = id,
        moodValue = moodValue,
        moodLabel = moodLabel,
        primaryFeelings = feelingsCsv.split(",").filter { it.isNotBlank() },
        note = note,
        dateLabel = dateLabel
    )

    companion object {
        fun fromDomain(checkIn: MoodCheckIn, timestamp: Long = System.currentTimeMillis()): MoodCheckInEntity = MoodCheckInEntity(
            id = checkIn.id,
            moodValue = checkIn.moodValue,
            moodLabel = checkIn.moodLabel,
            feelingsCsv = checkIn.primaryFeelings.joinToString(","),
            note = checkIn.note,
            dateLabel = checkIn.dateLabel,
            timestamp = timestamp
        )
    }
}

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val reflection: String,
    val moodScore: Int,
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): JournalEntry = JournalEntry(
        id = id,
        title = title,
        reflection = reflection,
        moodScore = moodScore,
        dateLabel = dateLabel,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(entry: JournalEntry): JournalEntryEntity = JournalEntryEntity(
            id = entry.id,
            title = entry.title,
            reflection = entry.reflection,
            moodScore = entry.moodScore,
            dateLabel = entry.dateLabel,
            timestamp = entry.timestamp
        )
    }
}

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val patientPhone: String,
    val practitionerId: String,
    val practitionerName: String,
    val practitionerTitle: String,
    val specialtyName: String,
    val serviceId: String,
    val serviceName: String,
    val dateIso: String,
    val timeSlotLabel: String,
    val consultationType: String,
    val locationDescription: String,
    val priceZmw: Double,
    val status: String,
    val paymentStatus: String,
    val intakeNotes: String? = null,
    val meetingRoomId: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis()
) {
    fun toDomain(): com.example.core.model.Appointment {
        val specialtyEnum = try {
            com.example.core.model.SpecialtyCategory.valueOf(specialtyName)
        } catch (_: Exception) {
            com.example.core.model.SpecialtyCategory.MENTAL_HEALTH
        }

        val typeEnum = try {
            com.example.core.model.ConsultationType.valueOf(consultationType)
        } catch (_: Exception) {
            com.example.core.model.ConsultationType.ONLINE
        }

        val statusEnum = try {
            com.example.core.model.AppointmentStatus.valueOf(status)
        } catch (_: Exception) {
            com.example.core.model.AppointmentStatus.CONFIRMED
        }

        val paymentEnum = try {
            com.example.core.model.PaymentStatus.valueOf(paymentStatus)
        } catch (_: Exception) {
            com.example.core.model.PaymentStatus.PAID
        }

        return com.example.core.model.Appointment(
            id = id,
            patientId = patientId,
            patientName = patientName,
            patientPhone = patientPhone,
            practitionerId = practitionerId,
            practitionerName = practitionerName,
            practitionerTitle = practitionerTitle,
            specialty = specialtyEnum,
            serviceId = serviceId,
            serviceName = serviceName,
            dateIso = dateIso,
            timeSlotLabel = timeSlotLabel,
            consultationType = typeEnum,
            locationDescription = locationDescription,
            priceZmw = priceZmw,
            status = statusEnum,
            paymentStatus = paymentEnum,
            intakeNotes = intakeNotes,
            meetingRoomId = meetingRoomId,
            createdAtEpoch = createdAtEpoch
        )
    }

    companion object {
        fun fromDomain(apt: com.example.core.model.Appointment): AppointmentEntity = AppointmentEntity(
            id = apt.id,
            patientId = apt.patientId,
            patientName = apt.patientName,
            patientPhone = apt.patientPhone,
            practitionerId = apt.practitionerId,
            practitionerName = apt.practitionerName,
            practitionerTitle = apt.practitionerTitle,
            specialtyName = apt.specialty.name,
            serviceId = apt.serviceId,
            serviceName = apt.serviceName,
            dateIso = apt.dateIso,
            timeSlotLabel = apt.timeSlotLabel,
            consultationType = apt.consultationType.name,
            locationDescription = apt.locationDescription,
            priceZmw = apt.priceZmw,
            status = apt.status.name,
            paymentStatus = apt.paymentStatus.name,
            intakeNotes = apt.intakeNotes,
            meetingRoomId = apt.meetingRoomId,
            createdAtEpoch = apt.createdAtEpoch
        )
    }
}

