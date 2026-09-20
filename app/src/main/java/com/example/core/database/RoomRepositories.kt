package com.example.core.database

import com.example.core.database.dao.CareGoalDao
import com.example.core.database.dao.JournalEntryDao
import com.example.core.database.dao.MoodCheckInDao
import com.example.core.database.dao.PatientDao
import com.example.core.database.entity.CareGoalEntity
import com.example.core.database.entity.JournalEntryEntity
import com.example.core.database.entity.MoodCheckInEntity
import com.example.core.database.entity.PatientEntity
import com.example.core.model.*
import com.example.core.repository.CareRepository
import com.example.core.repository.PatientRepository
import com.example.core.repository.mock.MockDataContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class RoomPatientRepository(
    private val patientDao: PatientDao
) : PatientRepository {

    private val consentsFlow = MutableStateFlow(MockDataContainer.initialConsents)

    override fun getCurrentPatient(): Flow<Patient> {
        return patientDao.getPatient("pat_demo_me").map { entity ->
            entity?.toDomain() ?: Patient(
                id = "pat_demo_me",
                fullName = "Kondwani Tembo",
                email = "kondwani.tembo@example.zm",
                phoneNumber = "+260 97 5543210",
                selectedCity = "Lusaka",
                preferredLanguage = "English",
                emergencyContactName = "Chileshe Tembo (Sister)",
                emergencyContactPhone = "+260 96 1122334",
                avatarPresetId = "preset_amber"
            )
        }
    }

    override suspend fun updatePatientProfile(
        name: String,
        phone: String,
        city: String,
        email: String?,
        emergencyName: String?,
        emergencyPhone: String?,
        avatarUri: String?,
        avatarPresetId: String?
    ) {
        val existing = patientDao.getPatient("pat_demo_me")
        patientDao.insertOrUpdate(
            PatientEntity(
                id = "pat_demo_me",
                fullName = name,
                phoneNumber = phone,
                selectedCity = city,
                email = email ?: "kondwani.tembo@example.zm",
                emergencyContactName = emergencyName,
                emergencyContactPhone = emergencyPhone,
                avatarUri = avatarUri,
                avatarPresetId = avatarPresetId
            )
        )
    }

    override fun getConsentRecords(): Flow<List<PatientConsentRecord>> = consentsFlow

    override suspend fun toggleConsent(consentType: String, isGranted: Boolean) {
        val current = consentsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.consentType == consentType }
        if (index != -1) {
            current[index] = current[index].copy(isGranted = isGranted)
            consentsFlow.value = current
        }
    }
}

class RoomCareRepository(
    private val careGoalDao: CareGoalDao,
    private val moodCheckInDao: MoodCheckInDao,
    private val journalEntryDao: JournalEntryDao
) : CareRepository {

    override fun getCareGoals(): Flow<List<CareGoal>> {
        return careGoalDao.getAllGoals().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun toggleGoalProgress(goalId: String, completed: Boolean) {
        val newProgress = if (completed) 100 else 0
        careGoalDao.updateGoalProgress(goalId, completed, newProgress)
    }

    override suspend fun addCareGoal(title: String, category: String, targetDescription: String) {
        val newGoal = CareGoalEntity(
            id = "goal_${UUID.randomUUID()}",
            title = title,
            category = category,
            progressPercent = 0,
            targetDescription = targetDescription,
            isCompleted = false
        )
        careGoalDao.insert(newGoal)
    }

    override fun getJournalEntries(): Flow<List<JournalEntry>> {
        return journalEntryDao.getJournalEntries().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addJournalEntry(title: String, reflection: String, moodScore: Int): JournalEntry {
        val dateFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
        val entry = JournalEntryEntity(
            id = "jrnl_${UUID.randomUUID()}",
            title = title,
            reflection = reflection,
            moodScore = moodScore,
            dateLabel = dateFmt,
            timestamp = System.currentTimeMillis()
        )
        journalEntryDao.insert(entry)
        return entry.toDomain()
    }

    override fun getMoodCheckIns(): Flow<List<MoodCheckIn>> {
        return moodCheckInDao.getMoodCheckIns().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun recordMoodCheckIn(
        moodValue: Int,
        moodLabel: String,
        feelings: List<String>,
        note: String?
    ): MoodCheckIn {
        val dateFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
        val checkIn = MoodCheckInEntity(
            id = "mood_${UUID.randomUUID()}",
            moodValue = moodValue,
            moodLabel = moodLabel,
            feelingsCsv = feelings.joinToString(","),
            note = note,
            dateLabel = dateFmt,
            timestamp = System.currentTimeMillis()
        )
        moodCheckInDao.insert(checkIn)
        return checkIn.toDomain()
    }
}

class RoomAppointmentRepository(
    private val appointmentDao: com.example.core.database.dao.AppointmentDao
) : com.example.core.repository.AppointmentRepository {

    override fun getAppointments(): Flow<List<Appointment>> {
        return appointmentDao.getAllAppointments().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAppointmentById(id: String): Appointment? {
        return appointmentDao.getAppointmentById(id)?.toDomain()
    }

    override suspend fun bookAppointment(
        practitioner: Practitioner,
        service: HealthcareService,
        dateIso: String,
        timeSlotLabel: String,
        consultationType: ConsultationType,
        intakeNotes: String?
    ): Appointment {
        val newAppointment = Appointment(
            id = "apt_${UUID.randomUUID().toString().take(8)}",
            patientId = "pat_demo_me",
            patientName = "Kondwani Tembo",
            patientPhone = "+260 97 5543210",
            practitionerId = practitioner.id,
            practitionerName = practitioner.fullName,
            practitionerTitle = practitioner.title,
            specialty = practitioner.primarySpecialty,
            serviceId = service.id,
            serviceName = service.name,
            dateIso = dateIso,
            timeSlotLabel = timeSlotLabel,
            consultationType = consultationType,
            locationDescription = if (consultationType == ConsultationType.ONLINE)
                "Encrypted Virtual Room"
            else
                (practitioner.practice?.location?.physicalAddress ?: "${practitioner.location.area}, ${practitioner.location.city}"),
            priceZmw = service.priceZmw,
            status = AppointmentStatus.PENDING_PAYMENT,
            paymentStatus = PaymentStatus.PENDING,
            intakeNotes = intakeNotes,
            meetingRoomId = if (consultationType == ConsultationType.ONLINE) "room_${UUID.randomUUID().toString().take(6)}" else null,
            createdAtEpoch = System.currentTimeMillis()
        )

        appointmentDao.insertOrUpdate(com.example.core.database.entity.AppointmentEntity.fromDomain(newAppointment))
        return newAppointment
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus): Boolean {
        val existing = appointmentDao.getAppointmentById(appointmentId) ?: return false
        val newPayStatus = if (newStatus == AppointmentStatus.CONFIRMED) PaymentStatus.PAID.name else existing.paymentStatus
        val rows = appointmentDao.updateStatusAndPayment(appointmentId, newStatus.name, newPayStatus)
        return rows > 0
    }

    override suspend fun cancelAppointment(appointmentId: String, reason: String?): Boolean {
        return updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
    }

    override suspend fun rescheduleAppointment(
        appointmentId: String,
        newDateIso: String,
        newTimeSlot: String
    ): Boolean {
        val rows = appointmentDao.rescheduleAppointment(appointmentId, newDateIso, newTimeSlot, AppointmentStatus.RESCHEDULED.name)
        return rows > 0
    }
}

