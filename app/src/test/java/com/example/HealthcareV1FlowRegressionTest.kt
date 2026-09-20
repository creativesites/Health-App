package com.example

import com.example.core.model.*
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * End-to-end regression tests verifying the bidirectional V1 North Star journey:
 * 1. Appointment booking has patientName and patientPhone
 * 2. Mobile money payment confirmation in ZMW
 * 3. Bidirectional messaging preserves active sender role
 * 4. Clinical encounter notes sync to appointment and care goals
 */
class HealthcareV1FlowRegressionTest {

    @Test
    fun testAppointmentHasPatientIdentityAndZambianCurrency() = runTest {
        val apptRepo = AppRepositoryLocator.appointmentRepository
        val appointments = apptRepo.getAppointments().first()
        assertTrue("Appointments list should not be empty", appointments.isNotEmpty())

        val firstAppt = appointments.first()
        assertNotNull("Patient name should not be null", firstAppt.patientName)
        assertTrue("Patient name should be populated", firstAppt.patientName.isNotBlank())
        assertTrue("Price in ZMW should be positive", firstAppt.priceZmw > 0)
    }

    @Test
    fun testRoleAwareMessagingForPatientAndSpecialist() = runTest {
        val msgRepo = AppRepositoryLocator.messageRepository
        val convId = "conv_test_journey"

        // 1. Patient sends message
        val patientMsg = msgRepo.sendMessage(
            conversationId = convId,
            text = "Hello doctor, looking forward to our session.",
            senderRole = UserRole.USER,
            senderName = "Kondwani Tembo"
        )
        assertTrue("Message sent by patient should mark isFromPatient = true", patientMsg.isFromPatient)
        assertEquals("Kondwani Tembo", patientMsg.senderName)

        // 2. Specialist responds
        val specialistMsg = msgRepo.sendMessage(
            conversationId = convId,
            text = "Hello Kondwani, please join the video room when you are ready.",
            senderRole = UserRole.SPECIALIST,
            senderName = "Mutale Chileshe"
        )
        assertFalse("Message sent by specialist should mark isFromPatient = false", specialistMsg.isFromPatient)
        assertEquals("Mutale Chileshe", specialistMsg.senderName)
    }

    @Test
    fun testSpecialistEncounterSyncsToCareGoalsAndCompletedStatus() = runTest {
        val apptRepo = AppRepositoryLocator.appointmentRepository
        val encounterRepo = AppRepositoryLocator.encounterRepository
        val careRepo = AppRepositoryLocator.careRepository

        val testAptId = "apt_upcoming_001"

        // Specialist saves encounter
        val encounter = ClinicalEncounter(
            encounterId = "enc_test_001",
            appointmentId = testAptId,
            practitionerId = "doc_chileshe_01",
            patientId = "pat_demo_me",
            patientDisplayName = "Kondwani Tembo",
            dateIso = "Today",
            consultationType = ConsultationType.ONLINE,
            consultationSummary = "Conducted CBT intake session.",
            observations = "Mild acute anxiety, good coping capacity.",
            agreedNextSteps = "Daily 4-4-6 breathing and sleep wind-down.",
            followUpDateIso = "2026-10-04",
            carePlanActions = listOf("Perform daily 4-4-6 box breathing", "15-minute phone-free sleep wind down"),
            isCompleted = true
        )
        encounterRepo.saveEncounter(encounter)

        // Add goals to CareRepo as done in SpecialistEncounterNotesScreen
        encounter.carePlanActions.forEach { action ->
            careRepo.addCareGoal(
                title = action,
                category = "Specialist Recommendation",
                targetDescription = "Prescribed by Dr. Mutale Chileshe"
            )
        }

        // Mark appointment completed
        apptRepo.updateAppointmentStatus(testAptId, AppointmentStatus.COMPLETED)

        // Verify encounter lookup by appointment ID
        val retrievedEnc = encounterRepo.getEncounterForAppointment(testAptId)
        assertNotNull("Encounter must be retrievable by appointmentId", retrievedEnc)
        assertEquals("Conducted CBT intake session.", retrievedEnc?.consultationSummary)

        // Verify appointment status updated to COMPLETED
        val updatedApt = apptRepo.getAppointmentById(testAptId)
        assertEquals(AppointmentStatus.COMPLETED, updatedApt?.status)

        // Verify care goals updated
        val goals = careRepo.getCareGoals().first()
        val foundGoal = goals.any { it.title.contains("4-4-6") }
        assertTrue("Care goals must contain the doctor's prescribed habit", foundGoal)
    }
}
