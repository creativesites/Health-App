package com.example.core.repository.mock

import com.example.core.model.*
import com.example.core.repository.*
import kotlinx.coroutines.flow.*
import java.util.UUID

/**
 * Production-shaped Mock Data layer.
 * Structured to cleanly swap with ApiPractitionerRepository, ApiAppointmentRepository,
 * and FastAPI backend without touching ViewModels or UI.
 *
 * All demo providers are explicitly marked with fictional identifiers.
 */
object MockDataContainer {

    val practices = listOf(
        Practice(
            id = "prac_lusaka_wellbeing",
            name = "Lusaka Integrated Wellness Clinic",
            location = ZambianLocation(city = "Lusaka", area = "Woodlands", physicalAddress = "Plot 4921, Lake Road, Woodlands"),
            phone = "+260 97 1234567",
            email = "info@lusakawellness.demo.zm",
            isVerified = true
        ),
        Practice(
            id = "prac_copperbelt_care",
            name = "Copperbelt Health & Therapy Associates",
            location = ZambianLocation(city = "Kitwe", area = "Riverside", physicalAddress = "Jambo Drive, Riverside, Kitwe"),
            phone = "+260 96 7654321",
            email = "kitwe@copperbelthealth.demo.zm",
            isVerified = true
        ),
        Practice(
            id = "prac_ndola_specialists",
            name = "Ndola Specialist Polyclinic",
            location = ZambianLocation(city = "Ndola", area = "Kansenshi", physicalAddress = "Broadway Road, Kansenshi"),
            phone = "+260 95 9876543",
            email = "care@ndolaspecialists.demo.zm",
            isVerified = true
        )
    )

    val practitioners = listOf(
        Practitioner(
            id = "doc_chileshe_01",
            fullName = "Mutale Chileshe",
            title = "Clinical Psychologist",
            credentials = listOf("HPCZ Registered (Demo)", "MSc Clinical Psychology (UNZA)", "10+ Yrs Experience"),
            primarySpecialty = SpecialtyCategory.MENTAL_HEALTH,
            secondarySpecialties = listOf(SpecialtyCategory.PSYCHIATRY),
            isVerified = true,
            verificationBody = "Health Professions Council of Zambia (HPCZ Demo Registry)",
            bio = "Warm, collaborative clinical psychologist specializing in stress management, cognitive behavioral therapy, anxiety, and family support. Practicing in Lusaka with both virtual and physical consultation options.",
            consultationStyle = "Empathetic, structured, culturally attuned, evidence-based CBT.",
            languages = listOf("English", "Bemba", "Nyanja"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Woodlands"),
            startingPriceZmw = 400.0,
            rating = 4.9,
            reviewCount = 38,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "MC",
            imageResId = com.example.R.drawable.doc_mwansa_chileshe
        ),
        Practitioner(
            id = "doc_mwansa_02",
            fullName = "Dr. Thandiwe Mwansa",
            title = "Specialist Psychiatrist",
            credentials = listOf("MBChB", "MMed Psychiatry", "HPCZ Specialist Licensure"),
            primarySpecialty = SpecialtyCategory.PSYCHIATRY,
            secondarySpecialties = listOf(SpecialtyCategory.MENTAL_HEALTH),
            isVerified = true,
            verificationBody = "HPCZ Specialist Medical Board",
            bio = "Senior consulting psychiatrist focused on adult mental wellness, mood regulation, clinical depression, and holistic patient-centered treatment plans.",
            consultationStyle = "Clinical precision combined with deep compassion and thorough diagnostic assessment.",
            languages = listOf("English", "Nyanja"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Rhodes Park"),
            startingPriceZmw = 550.0,
            rating = 4.8,
            reviewCount = 27,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "TM",
            imageResId = com.example.R.drawable.doc_thandiwe_zulu
        ),
        Practitioner(
            id = "doc_lungu_03",
            fullName = "Dr. Joseph Lungu",
            title = "General Practitioner",
            credentials = listOf("MBChB (CBU SOM)", "Postgrad Public Health", "HPCZ Registered"),
            primarySpecialty = SpecialtyCategory.GENERAL_PRACTICE,
            isVerified = true,
            verificationBody = "HPCZ Medical Board",
            bio = "Experienced primary care physician dedicated to preventative health, lifestyle medicine, hypertension management, and general family consultations across the Copperbelt.",
            consultationStyle = "Thorough, approachable, focused on clear patient education.",
            languages = listOf("English", "Bemba", "Lamba"),
            practice = practices[1],
            location = ZambianLocation(city = "Kitwe", area = "Riverside"),
            startingPriceZmw = 300.0,
            rating = 4.9,
            reviewCount = 52,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "JL",
            imageResId = com.example.R.drawable.doc_kachinga_phiri
        ),
        Practitioner(
            id = "doc_banda_04",
            fullName = "Chileshe Banda",
            title = "Senior Physiotherapist",
            credentials = listOf("BSc Physiotherapy", "Sports Injury Cert", "HPCZ Registered"),
            primarySpecialty = SpecialtyCategory.PHYSIOTHERAPY,
            isVerified = true,
            verificationBody = "HPCZ Allied Health Directorate",
            bio = "Specialist in musculoskeletal rehabilitation, chronic back pain recovery, postural alignment, and post-operative mobility therapy.",
            consultationStyle = "Active movement therapy, ergonomic coaching, hands-on rehab.",
            languages = listOf("English", "Nyanja", "Tonga"),
            practice = practices[2],
            location = ZambianLocation(city = "Ndola", area = "Kansenshi"),
            startingPriceZmw = 350.0,
            rating = 4.7,
            reviewCount = 19,
            supportedConsultationTypes = listOf(ConsultationType.IN_PERSON, ConsultationType.ONLINE),
            avatarInitials = "CB",
            imageResId = com.example.R.drawable.doc_mutale_kangwa
        ),
        Practitioner(
            id = "doc_zulu_05",
            fullName = "Nalukui Zulu",
            title = "Clinical Nutritionist & Dietitian",
            credentials = listOf("BSc Dietetics & Human Nutrition", "Diabetes Educator Cert"),
            primarySpecialty = SpecialtyCategory.NUTRITION,
            isVerified = true,
            verificationBody = "Nutritionists Association of Zambia / HPCZ",
            bio = "Certified clinical dietitian providing customized meal architectures for diabetes, metabolic health, digestive disorders, and maternal nutrition in Zambia.",
            consultationStyle = "Practical Zambian food-based nutrition plans, sustainable lifestyle habits.",
            languages = listOf("English", "Bemba", "Lozi"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Kabulonga"),
            startingPriceZmw = 350.0,
            rating = 4.9,
            reviewCount = 31,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "NZ",
            imageResId = com.example.R.drawable.doc_chileshe_tembo
        ),
        Practitioner(
            id = "doc_phiri_06",
            fullName = "Dr. Kondwani Phiri",
            title = "Dental Surgeon",
            credentials = listOf("BDS Dental Surgery", "HPCZ Dental Registry"),
            primarySpecialty = SpecialtyCategory.DENTAL,
            isVerified = true,
            verificationBody = "HPCZ Dental Council",
            bio = "Gentle, modern dental surgery specializing in preventative dentistry, oral health assessments, restorative care, and emergency pain relief.",
            consultationStyle = "Gentle, anxiety-free dentistry with modern digital imaging.",
            languages = listOf("English", "Nyanja", "Tumbuka"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Longacres"),
            startingPriceZmw = 450.0,
            rating = 4.8,
            reviewCount = 44,
            supportedConsultationTypes = listOf(ConsultationType.IN_PERSON),
            avatarInitials = "KP",
            imageResId = com.example.R.drawable.doc_kondwani_banda
        ),
        Practitioner(
            id = "doc_tembo_07",
            fullName = "Dr. Chileshe Tembo",
            title = "Specialist Pediatrician",
            credentials = listOf("MBChB (UNZA)", "MMed Pediatrics", "HPCZ Specialist Registry"),
            primarySpecialty = SpecialtyCategory.PEDIATRICS,
            isVerified = true,
            verificationBody = "HPCZ Specialist Medical Board",
            bio = "Dedicated pediatrician with over 8 years of clinical experience in neonatal care, childhood developmental milestones, immunization coordination, and pediatric disease management in Lusaka.",
            consultationStyle = "Approachable, play-based pediatric evaluations, highly reassuring for parents.",
            languages = listOf("English", "Bemba", "Nyanja"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Woodlands"),
            startingPriceZmw = 400.0,
            rating = 4.9,
            reviewCount = 32,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "CT",
            imageResId = com.example.R.drawable.doc_chileshe_tembo
        ),
        Practitioner(
            id = "doc_kangwa_08",
            fullName = "Dr. Mutale Kangwa",
            title = "Consultant Obstetrician & Gynecologist",
            credentials = listOf("MBChB (UNZA)", "MMed OBGYN", "HPCZ Specialist Registry"),
            primarySpecialty = SpecialtyCategory.WOMENS_HEALTH,
            isVerified = true,
            verificationBody = "HPCZ Specialist Medical Board",
            bio = "Expert gynecologist specializing in prenatal care, maternal health guidance, preventative wellness screenings, and menopause management.",
            consultationStyle = "Warm, thorough clinical assessments with dedicated patient support.",
            languages = listOf("English", "Bemba", "Nyanja"),
            practice = practices[0],
            location = ZambianLocation(city = "Lusaka", area = "Kabulonga"),
            startingPriceZmw = 500.0,
            rating = 4.9,
            reviewCount = 41,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON),
            avatarInitials = "MK",
            imageResId = com.example.R.drawable.doc_mutale_kangwa
        )
    )

    val services = listOf(
        HealthcareService(
            id = "srv_cbt_50",
            practitionerId = "doc_chileshe_01",
            name = "Individual Psychological Counselling",
            description = "Confidential 50-minute one-on-one session addressing stress, anxiety, life transitions, or mood concerns.",
            durationMinutes = 50,
            priceZmw = 400.0,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
        ),
        HealthcareService(
            id = "srv_couples_cbt",
            practitionerId = "doc_chileshe_01",
            name = "Relationship & Couples Support",
            description = "Structured 75-minute joint consultation focusing on communication dynamics and relationship health.",
            durationMinutes = 75,
            priceZmw = 600.0,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
        ),
        HealthcareService(
            id = "srv_psych_eval",
            practitionerId = "doc_mwansa_02",
            name = "Psychiatric Assessment & Medical Review",
            description = "Comprehensive clinical psychiatric diagnostic evaluation and medication review session.",
            durationMinutes = 60,
            priceZmw = 550.0,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
        ),
        HealthcareService(
            id = "srv_gp_telehealth",
            practitionerId = "doc_lungu_03",
            name = "General Medical Consultation",
            description = "Full general health review, symptom assessment, chronic medication review, or laboratory referral.",
            durationMinutes = 30,
            priceZmw = 300.0,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
        ),
        HealthcareService(
            id = "srv_physio_eval",
            practitionerId = "doc_banda_04",
            name = "Physiotherapy & Musculoskeletal Assessment",
            description = "Hands-on diagnostic assessment of joint, spine, or muscle impairment with targeted exercise prescription.",
            durationMinutes = 45,
            priceZmw = 350.0,
            supportedConsultationTypes = listOf(ConsultationType.IN_PERSON, ConsultationType.ONLINE)
        ),
        HealthcareService(
            id = "srv_nutrition_consult",
            practitionerId = "doc_zulu_05",
            name = "Personalized Dietary Care Plan",
            description = "Nutritional assessment, blood sugar & dietary analysis, and customized meal plan for local Zambian foods.",
            durationMinutes = 45,
            priceZmw = 350.0,
            supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
        ),
        HealthcareService(
            id = "srv_dental_checkup",
            practitionerId = "doc_phiri_06",
            name = "Comprehensive Dental Examination",
            description = "Thorough oral hygiene check, digital charting, screening, and diagnostic plan.",
            durationMinutes = 40,
            priceZmw = 450.0,
            supportedConsultationTypes = listOf(ConsultationType.IN_PERSON)
        )
    )

    val initialAppointments = listOf(
        Appointment(
            id = "apt_upcoming_001",
            patientId = "pat_demo_me",
            patientName = "Kondwani Tembo",
            patientPhone = "+260 97 5543210",
            practitionerId = "doc_chileshe_01",
            practitionerName = "Mutale Chileshe",
            practitionerTitle = "Clinical Psychologist",
            specialty = SpecialtyCategory.MENTAL_HEALTH,
            serviceId = "srv_cbt_50",
            serviceName = "Individual Psychological Counselling",
            dateIso = "Tomorrow",
            timeSlotLabel = "14:00 - 14:50",
            consultationType = ConsultationType.ONLINE,
            locationDescription = "Secure Video Consultation (Room #ZK-892)",
            priceZmw = 400.0,
            status = AppointmentStatus.CONFIRMED,
            paymentStatus = PaymentStatus.PAID,
            intakeNotes = "Follow up on sleep patterns and anxiety management techniques.",
            meetingRoomId = "consult_room_demo_982"
        ),
        Appointment(
            id = "apt_req_003",
            patientId = "pat_mwila_02",
            patientName = "Mwila Kangwa",
            patientPhone = "+260 96 8899001",
            practitionerId = "doc_chileshe_01",
            practitionerName = "Mutale Chileshe",
            practitionerTitle = "Clinical Psychologist",
            specialty = SpecialtyCategory.MENTAL_HEALTH,
            serviceId = "srv_cbt_50",
            serviceName = "Individual Psychological Counselling",
            dateIso = "Today",
            timeSlotLabel = "16:00 - 16:50",
            consultationType = ConsultationType.ONLINE,
            locationDescription = "Encrypted Virtual Consultation Room",
            priceZmw = 400.0,
            status = AppointmentStatus.PENDING_PAYMENT,
            paymentStatus = PaymentStatus.PENDING,
            intakeNotes = "Experiencing acute workplace burnout, elevated pulse, and tension headaches."
        ),
        Appointment(
            id = "apt_past_002",
            patientId = "pat_demo_me",
            patientName = "Kondwani Tembo",
            patientPhone = "+260 97 5543210",
            practitionerId = "doc_lungu_03",
            practitionerName = "Dr. Joseph Lungu",
            practitionerTitle = "General Practitioner",
            specialty = SpecialtyCategory.GENERAL_PRACTICE,
            serviceId = "srv_gp_telehealth",
            serviceName = "General Medical Consultation",
            dateIso = "Last week",
            timeSlotLabel = "10:00 - 10:30",
            consultationType = ConsultationType.ONLINE,
            locationDescription = "Online Video Room",
            priceZmw = 300.0,
            status = AppointmentStatus.COMPLETED,
            paymentStatus = PaymentStatus.PAID,
            intakeNotes = "Routine seasonal checkup and blood pressure review."
        ),
        Appointment(
            id = "apt_past_004",
            patientId = "pat_bwalya_03",
            patientName = "Bwalya & Natasha Phiri",
            patientPhone = "+260 95 3344556",
            practitionerId = "doc_chileshe_01",
            practitionerName = "Mutale Chileshe",
            practitionerTitle = "Clinical Psychologist",
            specialty = SpecialtyCategory.MENTAL_HEALTH,
            serviceId = "srv_couples_cbt",
            serviceName = "Relationship & Couples Support",
            dateIso = "3 days ago",
            timeSlotLabel = "11:00 - 12:15",
            consultationType = ConsultationType.IN_PERSON,
            locationDescription = "Plot 4921, Lake Road, Woodlands, Lusaka",
            priceZmw = 600.0,
            status = AppointmentStatus.COMPLETED,
            paymentStatus = PaymentStatus.PAID,
            intakeNotes = "Initial intake session on communication and joint stress management."
        )
    )

    val specialistPatients = listOf(
        SpecialistPatientSummary(
            patientId = "pat_demo_me",
            displayName = "Kondwani Tembo",
            age = 32,
            gender = "Male",
            city = "Lusaka",
            totalSessions = 4,
            lastSessionDate = "14 Sept 2026",
            nextSessionDate = "Tomorrow, 14:00",
            careStatus = "Active Care",
            outstandingFollowUp = true,
            contactNumberMasked = "+260 97 ••• 3210",
            intakeSummary = "Presents with work-related stress, racing thoughts at night, and intermittent tension headaches. Has responded well to 4-4-6 paced breathing and boundary-setting journaling."
        ),
        SpecialistPatientSummary(
            patientId = "pat_mwila_02",
            displayName = "Mwila Kangwa",
            age = 28,
            gender = "Female",
            city = "Lusaka",
            totalSessions = 1,
            lastSessionDate = "28 Aug 2026",
            nextSessionDate = "Today, 16:00",
            careStatus = "Initial Evaluation",
            outstandingFollowUp = false,
            contactNumberMasked = "+260 96 ••• 7891",
            intakeSummary = "Referred by GP for burnout symptoms and elevated stress. Requests teleconsultation."
        ),
        SpecialistPatientSummary(
            patientId = "pat_bwalya_03",
            displayName = "Bwalya & Natasha Phiri",
            age = 35,
            gender = "Couples",
            city = "Lusaka (Woodlands)",
            totalSessions = 2,
            lastSessionDate = "3 days ago",
            nextSessionDate = null,
            careStatus = "Maintenance",
            outstandingFollowUp = true,
            contactNumberMasked = "+260 95 ••• 4422",
            intakeSummary = "Communication enhancement and family schedule recalibration."
        )
    )

    val defaultAvailability = listOf(
        SpecialistAvailabilityDay("Monday", isEnabled = true, startTime = "08:30", endTime = "16:30", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = true),
        SpecialistAvailabilityDay("Tuesday", isEnabled = true, startTime = "08:30", endTime = "16:30", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = true),
        SpecialistAvailabilityDay("Wednesday", isEnabled = true, startTime = "09:00", endTime = "15:00", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = false),
        SpecialistAvailabilityDay("Thursday", isEnabled = true, startTime = "08:30", endTime = "17:00", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = true),
        SpecialistAvailabilityDay("Friday", isEnabled = true, startTime = "08:30", endTime = "14:00", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = true),
        SpecialistAvailabilityDay("Saturday", isEnabled = false, startTime = "09:00", endTime = "12:00", slotDurationMinutes = 50, allowsOnline = true, allowsInPerson = false),
        SpecialistAvailabilityDay("Sunday", isEnabled = false, startTime = "09:00", endTime = "12:00", slotDurationMinutes = 50, allowsOnline = false, allowsInPerson = false)
    )

    val initialEncounters = listOf(
        ClinicalEncounter(
            encounterId = "enc_past_004",
            appointmentId = "apt_past_004",
            practitionerId = "doc_chileshe_01",
            patientId = "pat_bwalya_03",
            patientDisplayName = "Bwalya & Natasha Phiri",
            dateIso = "3 days ago",
            consultationType = ConsultationType.IN_PERSON,
            consultationSummary = "Joint session on communication pacing and conflict de-escalation.",
            observations = "Both partners engaged actively. Demonstrated openness to structured conversational turn-taking.",
            agreedNextSteps = "1. Practice 15-minute uninterrupted listening check-in twice weekly.\n2. Schedule follow-up in 3 weeks.",
            followUpDateIso = "2026-10-10",
            carePlanActions = listOf("15-minute weekly structured listening", "Journaling triggers"),
            referralNote = null,
            privatePractitionerNotes = "Good rapport established. Recommend focusing next session on stress boundaries at work.",
            sharedPatientSummary = "Discussed conversational pacing techniques. Agreed to conduct two 15-minute listening check-ins this week.",
            isCompleted = true
        )
    )

    val initialGoals = listOf(
        CareGoal(
            id = "goal_01",
            title = "Sleep Consistency",
            category = "Sleep & Routine",
            progressPercent = 70,
            targetDescription = "Maintain consistent 10:30 PM wind-down routine 5 nights per week.",
            isCompleted = false
        ),
        CareGoal(
            id = "goal_02",
            title = "Daily Grounding Breathwork",
            category = "Stress Management",
            progressPercent = 100,
            targetDescription = "Complete 5-minute box breathing before starting the work day.",
            isCompleted = true
        ),
        CareGoal(
            id = "goal_03",
            title = "Hydration & Balanced Meals",
            category = "Lifestyle & Nutrition",
            progressPercent = 45,
            targetDescription = "Drink 2.5L water daily and prioritize fresh local vegetables.",
            isCompleted = false
        )
    )

    val initialJournal = listOf(
        JournalEntry(
            id = "j_01",
            title = "Reflecting after evening walk",
            reflection = "Felt noticeably calmer today after taking a 20-minute screen break at sunset. Noticing when tension builds in my shoulders helped me pause earlier.",
            moodScore = 4,
            dateLabel = "Yesterday, 19:30"
        ),
        JournalEntry(
            id = "j_02",
            title = "Work deadline tension",
            reflection = "Felt pressured around midday with multiple project deadlines. Remembered Dr. Chileshe's grounding technique: 5 things I can see, 4 I can touch.",
            moodScore = 3,
            dateLabel = "3 days ago"
        )
    )

    val initialMoods = listOf(
        MoodCheckIn(
            id = "m_01",
            moodValue = 4,
            moodLabel = "Calm & Grounded",
            primaryFeelings = listOf("Focused", "Hopeful", "Steady"),
            note = "Slept well last night and had a productive morning.",
            dateLabel = "Today"
        ),
        MoodCheckIn(
            id = "m_02",
            moodValue = 3,
            moodLabel = "Reflective",
            primaryFeelings = listOf("Busy", "Tired"),
            note = "Long commute in Lusaka traffic, but unwinding now.",
            dateLabel = "Yesterday"
        )
    )

    val initialConsents = listOf(
        PatientConsentRecord(
            consentType = "Healthcare Privacy & Clinical Records",
            purpose = "Ensures your sensitive healthcare and consultation notes are encrypted and restricted solely to treating clinicians.",
            version = "v1.2 (Zambia Data Protection Act Aligned)",
            isGranted = true,
            timestampFormatted = "Granted on registration",
            scope = "Clinical Encounters & Doctor-Patient Confidentiality"
        ),
        PatientConsentRecord(
            consentType = "Mobile Consultation Audio/Video Streaming",
            purpose = "Permits real-time encrypted peer audio-video data transmission for telehealth consultations. Never recorded by default.",
            version = "v1.0",
            isGranted = true,
            timestampFormatted = "Granted on registration",
            scope = "Active Consultation Room Sessions"
        ),
        PatientConsentRecord(
            consentType = "Discreet Care & Appointment Reminders",
            purpose = "Send non-sensitive SMS and app notifications regarding schedule and payment status without disclosing medical diagnosis.",
            version = "v1.1",
            isGranted = true,
            timestampFormatted = "Granted on registration",
            scope = "Privacy-safe Notifications"
        )
    )

    val safetyResources = listOf(
        SafetyResource(
            id = "safe_01",
            title = "National Mental Health Lifeline (Demo)",
            description = "Confidential 24/7 crisis support and psychological first aid in English, Bemba, and Nyanja.",
            contactNumber = "+260 97 000 0000 (Toll-free demo)",
            isCrisis24_7 = true,
            areaCoverage = "Nationwide (Zambia)",
            isDemoResource = true
        ),
        SafetyResource(
            id = "safe_02",
            title = "Emergency Medical Response",
            description = "National Emergency Ambulance & Paramedic Dispatch Service.",
            contactNumber = "992 / 999",
            isCrisis24_7 = true,
            areaCoverage = "Lusaka & Major Urban Hubs",
            isDemoResource = true
        ),
        SafetyResource(
            id = "safe_03",
            title = "University Teaching Hospital (UTH) Emergency",
            description = "24-Hour Trauma and Acute Adult Emergency Care Unit.",
            contactNumber = "+260 211 251451",
            isCrisis24_7 = true,
            areaCoverage = "Lusaka Province",
            isDemoResource = true
        ),
        SafetyResource(
            id = "safe_04",
            title = "Kitwe Teaching Hospital Emergency Center",
            description = "Copperbelt regional trauma and emergency psychiatric triage service.",
            contactNumber = "+260 212 221222",
            isCrisis24_7 = true,
            areaCoverage = "Copperbelt Province",
            isDemoResource = true
        )
    )

    val initialNotifications = listOf(
        NotificationItem(
            id = "notif_01",
            title = "Upcoming Consultation Reminder",
            safePreview = "You have an appointment scheduled for tomorrow at 14:00. Check in 5 minutes early.",
            category = NotificationCategory.APPOINTMENT,
            timeAgo = "1 hour ago",
            isRead = false
        ),
        NotificationItem(
            id = "notif_02",
            title = "Payment Receipt Confirmed",
            safePreview = "Mobile money transaction of K400.00 was verified successfully. Reference: MOMO-8912.",
            category = NotificationCategory.PAYMENT,
            timeAgo = "Yesterday",
            isRead = true
        ),
        NotificationItem(
            id = "notif_03",
            title = "Care Plan Review",
            safePreview = "Your care goal 'Sleep Consistency' is at 70% completion. Great progress this week!",
            category = NotificationCategory.CARE,
            timeAgo = "2 days ago",
            isRead = true
        )
    )
}

class MockPractitionerRepository : PractitionerRepository {
    private val practitionersFlow = MutableStateFlow(MockDataContainer.practitioners)

    override fun getPractitioners(): Flow<List<Practitioner>> = practitionersFlow

    override suspend fun getPractitionerById(id: String): Practitioner? {
        return MockDataContainer.practitioners.find { it.id == id }
    }

    override suspend fun getServices(practitionerId: String): List<HealthcareService> {
        return MockDataContainer.services.filter { it.practitionerId == practitionerId }
            .ifEmpty {
                // Return default general service if none explicitly tied
                listOf(
                    HealthcareService(
                        id = "srv_default_${practitionerId}",
                        practitionerId = practitionerId,
                        name = "Initial Clinical Consultation",
                        description = "Standard comprehensive 45-minute clinical appointment.",
                        durationMinutes = 45,
                        priceZmw = 350.0,
                        supportedConsultationTypes = listOf(ConsultationType.ONLINE, ConsultationType.IN_PERSON)
                    )
                )
            }
    }

    override suspend fun getAvailableTimeSlots(practitionerId: String, dateIso: String): List<TimeSlot> {
        val targetDate = try {
            java.time.LocalDate.parse(dateIso)
        } catch (_: Exception) {
            return emptyList()
        }

        val rulesFlow = try {
            AppRepositoryLocator.availabilityRepository.getAvailabilityRules(practitionerId)
        } catch (_: Exception) {
            null
        }

        val exceptionsFlow = try {
            AppRepositoryLocator.availabilityRepository.getAvailabilityExceptions(practitionerId)
        } catch (_: Exception) {
            null
        }

        val appointmentsFlow = try {
            AppRepositoryLocator.appointmentRepository.getAppointments()
        } catch (_: Exception) {
            null
        }

        val rules = rulesFlow?.firstOrNull() ?: emptyList()
        val exceptions = exceptionsFlow?.firstOrNull() ?: emptyList()
        val appointments = appointmentsFlow?.firstOrNull() ?: emptyList()

        return com.example.core.domain.SchedulingEngine.generateSlots(
            practitionerId = practitionerId,
            targetDate = targetDate,
            rules = rules,
            exceptions = exceptions,
            appointments = appointments,
            serviceDurationMinutes = 30 // Will dynamically read from selected service in real implementation
        )
    }

    override fun searchPractitioners(
        query: String,
        specialty: SpecialtyCategory?,
        city: String?,
        consultationType: ConsultationType?
    ): Flow<List<Practitioner>> {
        return practitionersFlow.map { list ->
            list.filter { p ->
                val matchesQuery = query.isBlank() ||
                        p.fullName.contains(query, ignoreCase = true) ||
                        p.title.contains(query, ignoreCase = true) ||
                        p.bio.contains(query, ignoreCase = true) ||
                        p.practice?.name?.contains(query, ignoreCase = true) == true

                val matchesSpecialty = specialty == null ||
                        p.primarySpecialty == specialty ||
                        p.secondarySpecialties.contains(specialty)

                val matchesCity = city.isNullOrBlank() ||
                        p.location.city.equals(city, ignoreCase = true)

                val matchesType = consultationType == null ||
                        p.supportedConsultationTypes.contains(consultationType)

                matchesQuery && matchesSpecialty && matchesCity && matchesType
            }
        }
    }
}

class MockAppointmentRepository : AppointmentRepository {
    private val appointmentsFlow = MutableStateFlow(MockDataContainer.initialAppointments)

    override fun getAppointments(): Flow<List<Appointment>> = appointmentsFlow

    override suspend fun getAppointmentById(id: String): Appointment? {
        return appointmentsFlow.value.find { it.id == id }
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
                "Encrypted Virtual Room" else (practitioner.practice?.location?.physicalAddress ?: "${practitioner.location.area}, ${practitioner.location.city}"),
            priceZmw = service.priceZmw,
            status = AppointmentStatus.PENDING_PAYMENT,
            paymentStatus = PaymentStatus.PENDING,
            intakeNotes = intakeNotes,
            meetingRoomId = if (consultationType == ConsultationType.ONLINE) "room_${UUID.randomUUID().toString().take(6)}" else null
        )

        appointmentsFlow.value = listOf(newAppointment) + appointmentsFlow.value
        return newAppointment
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus): Boolean {
        val current = appointmentsFlow.value
        val index = current.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            val updated = current.toMutableList()
            val old = updated[index]
            val newPayStatus = if (newStatus == AppointmentStatus.CONFIRMED) PaymentStatus.PAID else old.paymentStatus
            updated[index] = old.copy(status = newStatus, paymentStatus = newPayStatus)
            appointmentsFlow.value = updated
            return true
        }
        return false
    }

    override suspend fun cancelAppointment(appointmentId: String, reason: String?): Boolean {
        return updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
    }

    override suspend fun rescheduleAppointment(
        appointmentId: String,
        newDateIso: String,
        newTimeSlot: String
    ): Boolean {
        val current = appointmentsFlow.value
        val index = current.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            val updated = current.toMutableList()
            val old = updated[index]
            updated[index] = old.copy(
                dateIso = newDateIso,
                timeSlotLabel = newTimeSlot,
                status = AppointmentStatus.RESCHEDULED
            )
            appointmentsFlow.value = updated
            return true
        }
        return false
    }
}

class MockPaymentRepository : PaymentRepository {
    private val transactionsFlow = MutableStateFlow<List<PaymentTransaction>>(emptyList())

    override suspend fun initiateMobileMoneyPayment(
        appointmentId: String,
        provider: PaymentProviderType,
        phoneNumber: String,
        amountZmw: Double
    ): PaymentTransaction {
        val tx = PaymentTransaction(
            transactionId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
            appointmentId = appointmentId,
            provider = provider,
            phoneNumber = phoneNumber,
            amountZmw = amountZmw,
            status = PaymentStatus.PAID, // Simulated sandbox payment success
            referenceCode = "MOMO-${(100000..999999).random()}",
            isSandboxDemo = true,
            timestamp = System.currentTimeMillis()
        )
        transactionsFlow.value = listOf(tx) + transactionsFlow.value
        return tx
    }

    override suspend fun getTransaction(transactionId: String): PaymentTransaction? {
        return transactionsFlow.value.find { it.transactionId == transactionId }
    }

    override fun getTransactions(): Flow<List<PaymentTransaction>> = transactionsFlow
}

class MockCareRepository : CareRepository {
    private val goalsFlow = MutableStateFlow(MockDataContainer.initialGoals)
    private val journalFlow = MutableStateFlow(MockDataContainer.initialJournal)
    private val moodFlow = MutableStateFlow(MockDataContainer.initialMoods)

    override fun getCareGoals(): Flow<List<CareGoal>> = goalsFlow

    override suspend fun toggleGoalProgress(goalId: String, completed: Boolean) {
        val current = goalsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == goalId }
        if (index != -1) {
            val old = current[index]
            current[index] = old.copy(
                isCompleted = completed,
                progressPercent = if (completed) 100 else 50
            )
            goalsFlow.value = current
        }
    }

    override suspend fun addCareGoal(title: String, category: String, targetDescription: String) {
        val newGoal = CareGoal(
            id = "goal_${UUID.randomUUID().toString().take(6)}",
            title = title,
            category = category,
            targetDescription = targetDescription,
            progressPercent = 0,
            isCompleted = false
        )
        goalsFlow.value = listOf(newGoal) + goalsFlow.value
    }

    override fun getJournalEntries(): Flow<List<JournalEntry>> = journalFlow

    override suspend fun addJournalEntry(title: String, reflection: String, moodScore: Int): JournalEntry {
        val entry = JournalEntry(
            id = "j_${UUID.randomUUID().toString().take(6)}",
            title = title,
            reflection = reflection,
            moodScore = moodScore,
            dateLabel = "Just now"
        )
        journalFlow.value = listOf(entry) + journalFlow.value
        return entry
    }

    override fun getMoodCheckIns(): Flow<List<MoodCheckIn>> = moodFlow

    override suspend fun recordMoodCheckIn(
        moodValue: Int,
        moodLabel: String,
        feelings: List<String>,
        note: String?
    ): MoodCheckIn {
        val checkIn = MoodCheckIn(
            id = "m_${UUID.randomUUID().toString().take(6)}",
            moodValue = moodValue,
            moodLabel = moodLabel,
            primaryFeelings = feelings,
            note = note,
            dateLabel = "Today"
        )
        moodFlow.value = listOf(checkIn) + moodFlow.value
        return checkIn
    }
}

class MockMessageRepository : MessageRepository {
    private val messagesFlow = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "msg_01",
                senderId = "doc_chileshe_01",
                senderName = "Mutale Chileshe",
                isFromPatient = false,
                text = "Hello! Looking forward to our consultation tomorrow. Please make sure you are in a quiet, comfortable space where you feel safe speaking freely.",
                timestampFormatted = "Yesterday, 15:42"
            ),
            ChatMessage(
                id = "msg_02",
                senderId = "pat_demo_me",
                senderName = "Me",
                isFromPatient = true,
                text = "Thank you, Dr. Chileshe. I will be ready at 14:00. I have jotted down a few thoughts in my care notes to discuss.",
                timestampFormatted = "Yesterday, 16:10"
            )
        )
    )

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> = messagesFlow

    override suspend fun sendMessage(
        conversationId: String,
        text: String,
        senderRole: UserRole,
        senderName: String?,
        senderId: String?
    ): ChatMessage {
        val isFromPat = senderRole == UserRole.USER
        val resolvedId = senderId ?: if (isFromPat) "pat_demo_me" else "doc_chileshe_01"
        val resolvedName = senderName ?: if (isFromPat) "Kondwani Tembo" else "Mutale Chileshe"

        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(6)}",
            senderId = resolvedId,
            senderName = resolvedName,
            isFromPatient = isFromPat,
            text = text,
            timestampFormatted = "Just now"
        )
        messagesFlow.value = messagesFlow.value + newMsg
        return newMsg
    }
}

class MockNotificationRepository : NotificationRepository {
    private val notifFlow = MutableStateFlow(MockDataContainer.initialNotifications)

    override fun getNotifications(): Flow<List<NotificationItem>> = notifFlow

    override suspend fun markAsRead(notificationId: String) {
        val current = notifFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            current[index] = current[index].copy(isRead = true)
            notifFlow.value = current
        }
    }

    override suspend fun markAllAsRead() {
        notifFlow.value = notifFlow.value.map { it.copy(isRead = true) }
    }
}

class MockPatientRepository : PatientRepository {
    private val patientFlow = MutableStateFlow(
        Patient(
            id = "pat_demo_me",
            fullName = "Kondwani Tembo",
            email = "kondwani.tembo@example.zm",
            phoneNumber = "+260 97 5543210",
            selectedCity = "Lusaka",
            preferredLanguage = "English",
            emergencyContactName = "Chileshe Tembo (Sister)",
            emergencyContactPhone = "+260 96 1122334"
        )
    )

    private val consentsFlow = MutableStateFlow(MockDataContainer.initialConsents)

    override fun getCurrentPatient(): Flow<Patient> = patientFlow

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
        patientFlow.value = patientFlow.value.copy(
            fullName = name,
            phoneNumber = phone,
            selectedCity = city,
            email = email ?: patientFlow.value.email,
            emergencyContactName = emergencyName ?: patientFlow.value.emergencyContactName,
            emergencyContactPhone = emergencyPhone ?: patientFlow.value.emergencyContactPhone,
            avatarUri = avatarUri ?: patientFlow.value.avatarUri,
            avatarPresetId = avatarPresetId ?: patientFlow.value.avatarPresetId
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

class MockSafetyRepository : SafetyRepository {
    override fun getSafetyResources(): List<SafetyResource> = MockDataContainer.safetyResources
}

class MockAuthRepository : AuthRepository {
    private val sessionFlow = MutableStateFlow<AuthSession?>(null)

    override fun getActiveSession(): Flow<AuthSession?> = sessionFlow

    override suspend fun loginAsUser(): AuthSession {
        val userSession = AuthSession(
            userId = "pat_demo_me",
            role = UserRole.USER,
            displayName = "Kondwani Tembo",
            avatarInitials = "KT",
            facilityName = "Lusaka"
        )
        sessionFlow.value = userSession
        return userSession
    }

    override suspend fun loginAsSpecialist(practitionerId: String): AuthSession {
        val practitioner = MockDataContainer.practitioners.find { it.id == practitionerId }
            ?: MockDataContainer.practitioners.first()
        val specialistSession = AuthSession(
            userId = practitioner.id,
            role = UserRole.SPECIALIST,
            displayName = practitioner.fullName,
            professionalTitle = practitioner.title,
            specialty = practitioner.primarySpecialty,
            facilityName = practitioner.practice?.name ?: "Lusaka Integrated Wellness Clinic",
            avatarInitials = practitioner.avatarInitials,
            practitionerId = practitioner.id
        )
        sessionFlow.value = specialistSession
        return specialistSession
    }

    override suspend fun logout() {
        sessionFlow.value = null
    }

    override fun getCurrentRole(): UserRole? = sessionFlow.value?.role
}

class MockSpecialistRepository : SpecialistRepository {
    private val patientsFlow = MutableStateFlow(MockDataContainer.specialistPatients)
    private val practitionerProfileFlow = MutableStateFlow(MockDataContainer.practitioners.first())

    override fun getSpecialistProfile(practitionerId: String): Flow<Practitioner?> {
        return practitionerProfileFlow.map { it.takeIf { p -> p.id == practitionerId } ?: MockDataContainer.practitioners.find { p -> p.id == practitionerId } }
    }

    override suspend fun updateSpecialistProfile(
        practitionerId: String,
        fullName: String,
        title: String,
        bio: String,
        languages: List<String>,
        supportedTypes: List<ConsultationType>
    ): Boolean {
        if (practitionerProfileFlow.value.id == practitionerId) {
            practitionerProfileFlow.value = practitionerProfileFlow.value.copy(
                fullName = fullName,
                title = title,
                bio = bio,
                languages = languages,
                supportedConsultationTypes = supportedTypes
            )
            return true
        }
        return false
    }

    override fun getSpecialistPatients(practitionerId: String): Flow<List<SpecialistPatientSummary>> {
        val apptFlow = try {
            AppRepositoryLocator.appointmentRepository.getAppointments()
        } catch (_: Exception) {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }

        return combine(patientsFlow, apptFlow) { seededList, apptList ->
            val practitionerAppts = apptList.filter { it.practitionerId == practitionerId }
            val dynamicPatients = practitionerAppts.map { apt ->
                SpecialistPatientSummary(
                    patientId = apt.patientId,
                    displayName = apt.patientName,
                    age = 30,
                    gender = "Client",
                    city = "Lusaka",
                    totalSessions = 1,
                    lastSessionDate = apt.dateIso,
                    nextSessionDate = "${apt.dateIso}, ${apt.timeSlotLabel}",
                    careStatus = if (apt.status == AppointmentStatus.CONFIRMED) "Active Care" else if (apt.status == AppointmentStatus.COMPLETED) "Completed" else "Initial Evaluation",
                    outstandingFollowUp = apt.status == AppointmentStatus.CONFIRMED,
                    contactNumberMasked = "+260 97 ••• 0000",
                    intakeSummary = apt.intakeNotes?.ifBlank { "Initial consultation booked via platform" } ?: "Consultation booked via platform (${apt.consultationType.name})"
                )
            }

            // Deduplicate by patientId, prioritizing latest dynamic appointment
            val map = linkedMapOf<String, SpecialistPatientSummary>()
            dynamicPatients.forEach { map[it.patientId] = it }
            seededList.forEach { if (!map.containsKey(it.patientId)) map[it.patientId] = it }
            map.values.toList()
        }
    }

    override suspend fun getPatientSummary(patientId: String): SpecialistPatientSummary? {
        val seeded = patientsFlow.value.find { it.patientId == patientId }
        if (seeded != null) return seeded

        return try {
            val apts = AppRepositoryLocator.appointmentRepository.getAppointments().first()
            val match = apts.find { it.patientId == patientId }
            if (match != null) {
                SpecialistPatientSummary(
                    patientId = match.patientId,
                    displayName = match.patientName,
                    age = 30,
                    gender = "Client",
                    city = "Lusaka",
                    totalSessions = 1,
                    lastSessionDate = match.dateIso,
                    nextSessionDate = "${match.dateIso}, ${match.timeSlotLabel}",
                    careStatus = if (match.status == AppointmentStatus.CONFIRMED) "Active Care" else if (match.status == AppointmentStatus.COMPLETED) "Completed" else "Initial Evaluation",
                    outstandingFollowUp = match.status == AppointmentStatus.CONFIRMED,
                    contactNumberMasked = "+260 97 ••• 0000",
                    intakeSummary = match.intakeNotes?.ifBlank { "Initial consultation booked via platform" } ?: "Consultation booked via platform (${match.consultationType.name})"
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }
}

class MockAvailabilityRepository : AvailabilityRepository {
    private val availabilityMap = mutableMapOf<String, MutableStateFlow<List<SpecialistAvailabilityDay>>>()

    // Temporary lists to satisfy new Scheduling Engine flow without a real database yet
    private val mockRulesFlow = MutableStateFlow(
        listOf(
            SpecialistAvailabilityRule(
                id = "rule_mock_mon_1",
                practitionerId = "doc_chileshe_01",
                dayOfWeek = "Monday",
                startTime = "09:00",
                endTime = "16:00",
                slotDurationMinutes = 30,
                bufferMinutes = 10,
                enabled = true,
                allowsOnline = true,
                allowsInPerson = true
            ),
            SpecialistAvailabilityRule(
                id = "rule_mock_tue_1",
                practitionerId = "doc_chileshe_01",
                dayOfWeek = "Tuesday",
                startTime = "09:00",
                endTime = "16:00",
                slotDurationMinutes = 30,
                bufferMinutes = 10,
                enabled = true,
                allowsOnline = true,
                allowsInPerson = true
            )
        )
    )
    private val mockExceptionsFlow = MutableStateFlow<List<AvailabilityException>>(emptyList())

    private fun getFlowFor(practitionerId: String): MutableStateFlow<List<SpecialistAvailabilityDay>> {
        return availabilityMap.getOrPut(practitionerId) {
            MutableStateFlow(MockDataContainer.defaultAvailability)
        }
    }

    override fun getAvailability(practitionerId: String): Flow<List<SpecialistAvailabilityDay>> {
        return getFlowFor(practitionerId)
    }

    override suspend fun updateDayAvailability(
        practitionerId: String,
        day: SpecialistAvailabilityDay
    ) {
        val flow = getFlowFor(practitionerId)
        val current = flow.value.toMutableList()
        val index = current.indexOfFirst { it.dayOfWeek.equals(day.dayOfWeek, ignoreCase = true) }
        if (index != -1) {
            current[index] = day
            flow.value = current
        }
    }

    override fun getAvailabilityRules(practitionerId: String): Flow<List<SpecialistAvailabilityRule>> {
        return mockRulesFlow.map { rules ->
            val practitionerRules = rules.filter { it.practitionerId == practitionerId }
            if (practitionerRules.isEmpty()) {
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday").map { day ->
                    SpecialistAvailabilityRule(
                        id = "rule_${practitionerId}_${day.take(3).lowercase()}",
                        practitionerId = practitionerId,
                        dayOfWeek = day,
                        startTime = "09:00",
                        endTime = "16:00",
                        slotDurationMinutes = 50,
                        bufferMinutes = 10,
                        enabled = true,
                        allowsOnline = true,
                        allowsInPerson = true
                    )
                }
            } else {
                practitionerRules
            }
        }
    }

    override fun getAvailabilityExceptions(practitionerId: String): Flow<List<AvailabilityException>> {
        return mockExceptionsFlow
    }

    override suspend fun saveRule(rule: SpecialistAvailabilityRule) {
        val current = mockRulesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == rule.id }
        if (index != -1) {
            current[index] = rule
        } else {
            current.add(rule)
        }
        mockRulesFlow.value = current
    }

    override suspend fun saveException(exception: AvailabilityException) {
        val current = mockExceptionsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == exception.id }
        if (index != -1) {
            current[index] = exception
        } else {
            current.add(exception)
        }
        mockExceptionsFlow.value = current
    }
}

class MockEncounterRepository : EncounterRepository {
    private val encountersFlow = MutableStateFlow(MockDataContainer.initialEncounters)

    override fun getEncountersForSpecialist(practitionerId: String): Flow<List<ClinicalEncounter>> {
        return encountersFlow.map { list -> list.filter { it.practitionerId == practitionerId } }
    }

    override fun getEncountersForPatient(patientId: String): Flow<List<ClinicalEncounter>> {
        return encountersFlow.map { list -> list.filter { it.patientId == patientId } }
    }

    override suspend fun getEncounterForAppointment(appointmentId: String): ClinicalEncounter? {
        return encountersFlow.value.find { it.appointmentId == appointmentId }
    }

    override suspend fun saveEncounter(encounter: ClinicalEncounter): ClinicalEncounter {
        val current = encountersFlow.value.toMutableList()
        val index = current.indexOfFirst { it.encounterId == encounter.encounterId || it.appointmentId == encounter.appointmentId }
        if (index != -1) {
            current[index] = encounter
        } else {
            current.add(0, encounter)
        }
        encountersFlow.value = current
        return encounter
    }
}

/**
 * Service Locator / Repository Provider.
 * Allows clean inversion of control and simplifies ViewModel instantiations
 * without bulky DI runtime setup, while remaining 100% prepared for Hilt/Koin.
 */
object AppRepositoryLocator {
    private var customPatientRepo: PatientRepository? = null
    private var customCareRepo: CareRepository? = null
    private var customAppointmentRepo: AppointmentRepository? = null

    fun initializeDatabase(database: com.example.core.database.AppDatabase) {
        customPatientRepo = com.example.core.database.RoomPatientRepository(database.patientDao())
        customCareRepo = com.example.core.database.RoomCareRepository(
            database.careGoalDao(),
            database.moodCheckInDao(),
            database.journalEntryDao()
        )
        customAppointmentRepo = com.example.core.database.RoomAppointmentRepository(
            database.appointmentDao()
        )
    }

    val authRepository: AuthRepository by lazy { MockAuthRepository() }
    val specialistRepository: SpecialistRepository by lazy { MockSpecialistRepository() }
    val availabilityRepository: AvailabilityRepository by lazy { MockAvailabilityRepository() }
    val encounterRepository: EncounterRepository by lazy { MockEncounterRepository() }

    val practitionerRepository: PractitionerRepository by lazy { MockPractitionerRepository() }
    private val defaultAppointmentRepo by lazy { MockAppointmentRepository() }
    val appointmentRepository: AppointmentRepository
        get() = customAppointmentRepo ?: defaultAppointmentRepo
    val paymentRepository: PaymentRepository by lazy { MockPaymentRepository() }
    private val defaultCareRepo by lazy { MockCareRepository() }
    val careRepository: CareRepository
        get() = customCareRepo ?: defaultCareRepo
    val messageRepository: MessageRepository by lazy { MockMessageRepository() }
    val notificationRepository: NotificationRepository by lazy { MockNotificationRepository() }
    private val defaultPatientRepo by lazy { MockPatientRepository() }
    val patientRepository: PatientRepository
        get() = customPatientRepo ?: defaultPatientRepo
    val safetyRepository: SafetyRepository by lazy { MockSafetyRepository() }
}
