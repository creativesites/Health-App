package com.example.core.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Healthcare platform domain models.
 * Generic healthcare infrastructure first; specialty experiences layered cleanly on top.
 */

enum class SpecialtyCategory(val displayName: String, val iconName: String) {
    MENTAL_HEALTH("Mental Health", "psychology"),
    GENERAL_PRACTICE("General Practice", "medical_services"),
    CARDIOLOGY("Cardiology", "favorite"),
    DERMATOLOGY("Dermatology", "spa"),
    PEDIATRICS("Pediatrics & Child Health", "child_care"),
    WOMENS_HEALTH("Women's Health", "pregnant_woman"),
    DENTAL("Dental Care", "dentistry"),
    NUTRITION("Nutrition & Diet", "nutrition"),
    PHYSIOTHERAPY("Physiotherapy", "fitness_center"),
    PSYCHIATRY("Psychiatry", "health_and_safety"),
    LABORATORY("Laboratory Services", "biotech"),
    OCCUPATIONAL_THERAPY("Occupational Therapy", "accessibility"),
    SPEECH_THERAPY("Speech Therapy", "record_voice_over"),
    OTHER("Specialist Care", "local_hospital");

    fun getIconRes(): Int = when (this) {
        MENTAL_HEALTH -> com.example.R.drawable.ic_spec_mental_health
        GENERAL_PRACTICE -> com.example.R.drawable.ic_spec_general_medicine
        CARDIOLOGY -> com.example.R.drawable.ic_spec_cardiology
        DERMATOLOGY -> com.example.R.drawable.ic_spec_dermatology
        PEDIATRICS -> com.example.R.drawable.ic_spec_pediatrics
        WOMENS_HEALTH -> com.example.R.drawable.ic_spec_womens_health
        DENTAL -> com.example.R.drawable.ic_spec_dental
        NUTRITION -> com.example.R.drawable.ic_spec_nutrition
        PHYSIOTHERAPY -> com.example.R.drawable.ic_spec_physiotherapy
        PSYCHIATRY -> com.example.R.drawable.ic_spec_mental_health
        LABORATORY -> com.example.R.drawable.ic_spec_laboratory
        OCCUPATIONAL_THERAPY -> com.example.R.drawable.ic_spec_physiotherapy
        SPEECH_THERAPY -> com.example.R.drawable.ic_spec_pediatrics
        OTHER -> com.example.R.drawable.ic_spec_general_medicine
    }
}

enum class ConsultationType(val label: String) {
    ONLINE("Online Video / Audio"),
    IN_PERSON("In-Person Clinic Visit")
}

enum class AppointmentStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_PAYMENT("Payment Pending"),
    CONFIRMED("Confirmed"),
    RESCHEDULE_REQUESTED("Reschedule Requested"),
    RESCHEDULED("Rescheduled"),
    CHECKED_IN("Checked In"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show"),
    REFUND_PENDING("Refund Pending"),
    REFUNDED("Refunded")
}

enum class PaymentProviderType(val displayName: String, val networkBrand: String) {
    MTN_MOMO("MTN Mobile Money", "MTN Zambia"),
    AIRTEL_MONEY("Airtel Money", "Airtel Zambia"),
    ZAMTEL_KWACHA("Zamtel Kwacha", "Zamtel"),
    DEBIT_CARD("Card Payment", "Visa / Mastercard")
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    CANCELLED,
    REFUNDED
}

data class ZambianLocation(
    val city: String, // e.g. "Lusaka", "Kitwe", "Ndola", "Livingstone", "Kabwe"
    val area: String, // e.g. "Woodlands", "Rhodes Park", "Riverside", "Town Centre"
    val physicalAddress: String? = null
)

data class Practice(
    val id: String,
    val name: String,
    val location: ZambianLocation,
    val phone: String,
    val email: String,
    val isVerified: Boolean = true
)

data class HealthcareService(
    val id: String,
    val practitionerId: String,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val priceZmw: Double, // Zambian Kwacha
    val supportedConsultationTypes: List<ConsultationType> = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
)

data class Practitioner(
    val id: String,
    val fullName: String,
    val title: String, // e.g. "Dr.", "Counsellor", "Clinical Psychologist"
    val credentials: List<String>, // e.g. ["HPCZ Registered", "MSc Clinical Psychology"]
    val primarySpecialty: SpecialtyCategory,
    val secondarySpecialties: List<SpecialtyCategory> = emptyList(),
    val isVerified: Boolean,
    val verificationBody: String = "Health Professions Council of Zambia (HPCZ)",
    val bio: String,
    val consultationStyle: String,
    val languages: List<String>, // e.g. ["English", "Bemba", "Nyanja"]
    val practice: Practice?,
    val location: ZambianLocation,
    val startingPriceZmw: Double,
    val rating: Double,
    val reviewCount: Int,
    val supportedConsultationTypes: List<ConsultationType>,
    val avatarInitials: String = "DR",
    val imageResId: Int? = null
)

data class TimeSlot(
    val id: String,
    val practitionerId: String,
    val dateIso: String, // YYYY-MM-DD
    val timeLabel: String, // e.g. "09:00 - 09:50", "14:00 - 14:50"
    val isAvailable: Boolean = true
)

data class Appointment(
    val id: String,
    val patientId: String,
    val patientName: String = "Kondwani Tembo",
    val patientPhone: String = "+260 97 1234567",
    val practitionerId: String,
    val practitionerName: String,
    val practitionerTitle: String,
    val specialty: SpecialtyCategory,
    val serviceId: String,
    val serviceName: String,
    val dateIso: String,
    val timeSlotLabel: String,
    val consultationType: ConsultationType,
    val locationDescription: String,
    val priceZmw: Double,
    val status: AppointmentStatus,
    val paymentStatus: PaymentStatus,
    val intakeNotes: String? = null,
    val meetingRoomId: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

data class PaymentTransaction(
    val transactionId: String,
    val appointmentId: String,
    val provider: PaymentProviderType,
    val phoneNumber: String,
    val amountZmw: Double,
    val status: PaymentStatus,
    val referenceCode: String,
    val isSandboxDemo: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class Patient(
    val id: String,
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
    val initials: String
        get() = fullName.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
            .take(2)
            .joinToString("")
            .ifEmpty { "ME" }
}

data class CareGoal(
    val id: String,
    val title: String,
    val category: String,
    val progressPercent: Int, // 0..100
    val targetDescription: String,
    val isCompleted: Boolean = false
)

data class JournalEntry(
    val id: String,
    val title: String,
    val reflection: String,
    val moodScore: Int, // 1 to 5
    val dateLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MoodCheckIn(
    val id: String,
    val moodValue: Int, // 1 (Distressed) to 5 (Thriving)
    val moodLabel: String,
    val primaryFeelings: List<String>,
    val note: String?,
    val dateLabel: String
)

data class PatientConsentRecord(
    val consentType: String,
    val purpose: String,
    val version: String,
    val isGranted: Boolean,
    val timestampFormatted: String,
    val scope: String
)

enum class NotificationCategory {
    APPOINTMENT,
    PAYMENT,
    MESSAGE,
    CARE,
    SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val safePreview: String, // Clean non-clinical notification to protect privacy
    val category: NotificationCategory,
    val timeAgo: String,
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val isFromPatient: Boolean,
    val text: String,
    val timestampFormatted: String,
    val isRead: Boolean = true
)

data class SafetyResource(
    val id: String,
    val title: String,
    val description: String,
    val contactNumber: String,
    val isCrisis24_7: Boolean,
    val areaCoverage: String,
    val isDemoResource: Boolean = true
)

// ==========================================
// Role-Based Authentication & Session Models
// ==========================================

enum class UserRole {
    USER,       // Patient / Citizen seeking care
    SPECIALIST  // Healthcare Practitioner / Specialist
}

data class AuthSession(
    val userId: String,
    val role: UserRole,
    val displayName: String,
    val professionalTitle: String? = null,
    val specialty: SpecialtyCategory? = null,
    val facilityName: String? = null,
    val avatarInitials: String = "US",
    val practitionerId: String? = null
)

// ==========================================
// Specialist Clinical & Operational Models
// ==========================================

data class SpecialistAvailabilityDay(
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val isEnabled: Boolean = true,
    val startTime: String = "08:30",
    val endTime: String = "16:30",
    val slotDurationMinutes: Int = 45,
    val bufferMinutes: Int = 10,
    val allowsOnline: Boolean = true,
    val allowsInPerson: Boolean = true
)

data class SpecialistAvailabilityRule(
    val id: String,
    val practitionerId: String,
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val startTime: String, // "HH:mm"
    val endTime: String,   // "HH:mm"
    val slotDurationMinutes: Int = 30,
    val bufferMinutes: Int = 10,
    val enabled: Boolean = true,
    val allowsOnline: Boolean = true,
    val allowsInPerson: Boolean = true
)

data class AvailabilityException(
    val id: String,
    val practitionerId: String,
    val dateIso: String, // "YYYY-MM-DD"
    val startTime: String? = null, // null means whole day is blocked
    val endTime: String? = null,
    val type: String = "BLOCKED", // "BLOCKED" or "ADDITIONAL_AVAILABILITY"
    val reason: String? = null,
    val enabled: Boolean = true
)

data class SpecialistPatientSummary(
    val patientId: String,
    val displayName: String,
    val age: Int,
    val gender: String,
    val city: String,
    val totalSessions: Int,
    val lastSessionDate: String,
    val nextSessionDate: String?,
    val careStatus: String, // "Active Care", "Maintenance", "Initial Evaluation"
    val outstandingFollowUp: Boolean,
    val contactNumberMasked: String,
    val intakeSummary: String
)

data class ClinicalEncounter(
    val encounterId: String,
    val appointmentId: String,
    val practitionerId: String,
    val patientId: String,
    val patientDisplayName: String,
    val dateIso: String,
    val consultationType: ConsultationType,
    val consultationSummary: String,
    val observations: String,
    val agreedNextSteps: String,
    val followUpDateIso: String?,
    val carePlanActions: List<String>,
    val referralNote: String? = null,
    val privatePractitionerNotes: String = "", // Kept strictly private to specialist
    val sharedPatientSummary: String = "",      // Shared with patient in their care dashboard
    val isCompleted: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
