package com.example.core.repository

import com.example.core.model.*
import kotlinx.coroutines.flow.Flow

interface PractitionerRepository {
    fun getPractitioners(): Flow<List<Practitioner>>
    suspend fun getPractitionerById(id: String): Practitioner?
    suspend fun getServices(practitionerId: String): List<HealthcareService>
    suspend fun getAvailableTimeSlots(practitionerId: String, dateIso: String): List<TimeSlot>
    fun searchPractitioners(query: String, specialty: SpecialtyCategory?, city: String?, consultationType: ConsultationType?): Flow<List<Practitioner>>
}

interface AppointmentRepository {
    fun getAppointments(): Flow<List<Appointment>>
    suspend fun getAppointmentById(id: String): Appointment?
    suspend fun bookAppointment(
        practitioner: Practitioner,
        service: HealthcareService,
        dateIso: String,
        timeSlotLabel: String,
        consultationType: ConsultationType,
        intakeNotes: String?
    ): Appointment
    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus): Boolean
    suspend fun cancelAppointment(appointmentId: String, reason: String?): Boolean
    suspend fun rescheduleAppointment(appointmentId: String, newDateIso: String, newTimeSlot: String): Boolean
}

interface PaymentRepository {
    suspend fun initiateMobileMoneyPayment(
        appointmentId: String,
        provider: PaymentProviderType,
        phoneNumber: String,
        amountZmw: Double
    ): PaymentTransaction

    suspend fun getTransaction(transactionId: String): PaymentTransaction?
    fun getTransactions(): Flow<List<PaymentTransaction>>
}

interface CareRepository {
    fun getCareGoals(): Flow<List<CareGoal>>
    suspend fun toggleGoalProgress(goalId: String, completed: Boolean)
    suspend fun addCareGoal(title: String, category: String, targetDescription: String) {}
    fun getJournalEntries(): Flow<List<JournalEntry>>
    suspend fun addJournalEntry(title: String, reflection: String, moodScore: Int): JournalEntry
    fun getMoodCheckIns(): Flow<List<MoodCheckIn>>
    suspend fun recordMoodCheckIn(moodValue: Int, moodLabel: String, feelings: List<String>, note: String?): MoodCheckIn
}

interface MessageRepository {
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(
        conversationId: String,
        text: String,
        senderRole: UserRole = UserRole.USER,
        senderName: String? = null,
        senderId: String? = null
    ): ChatMessage
}

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationItem>>
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
}

interface PatientRepository {
    fun getCurrentPatient(): Flow<Patient>
    suspend fun updatePatientProfile(
        name: String,
        phone: String,
        city: String,
        email: String? = null,
        emergencyName: String? = null,
        emergencyPhone: String? = null,
        avatarUri: String? = null,
        avatarPresetId: String? = null
    )
    fun getConsentRecords(): Flow<List<PatientConsentRecord>>
    suspend fun toggleConsent(consentType: String, isGranted: Boolean)
}

interface SafetyRepository {
    fun getSafetyResources(): List<SafetyResource>
}

interface AuthRepository {
    fun getActiveSession(): Flow<AuthSession?>
    suspend fun loginAsUser(): AuthSession
    suspend fun loginAsSpecialist(practitionerId: String = "doc_chileshe_01"): AuthSession
    suspend fun logout()
    fun getCurrentRole(): UserRole?
}

interface SpecialistRepository {
    fun getSpecialistProfile(practitionerId: String): Flow<Practitioner?>
    suspend fun updateSpecialistProfile(
        practitionerId: String,
        fullName: String,
        title: String,
        bio: String,
        languages: List<String>,
        supportedTypes: List<ConsultationType>
    ): Boolean
    fun getSpecialistPatients(practitionerId: String): Flow<List<SpecialistPatientSummary>>
    suspend fun getPatientSummary(patientId: String): SpecialistPatientSummary?
}

interface AvailabilityRepository {
    fun getAvailability(practitionerId: String): Flow<List<SpecialistAvailabilityDay>> // Legacy V1 signature
    suspend fun updateDayAvailability(practitionerId: String, day: SpecialistAvailabilityDay)
    
    // New Scheduling Engine Signatures
    fun getAvailabilityRules(practitionerId: String): Flow<List<SpecialistAvailabilityRule>>
    fun getAvailabilityExceptions(practitionerId: String): Flow<List<AvailabilityException>>
    suspend fun saveRule(rule: SpecialistAvailabilityRule)
    suspend fun saveException(exception: AvailabilityException)
}

interface EncounterRepository {
    fun getEncountersForSpecialist(practitionerId: String): Flow<List<ClinicalEncounter>>
    fun getEncountersForPatient(patientId: String): Flow<List<ClinicalEncounter>>
    suspend fun getEncounterForAppointment(appointmentId: String): ClinicalEncounter?
    suspend fun saveEncounter(encounter: ClinicalEncounter): ClinicalEncounter
}
