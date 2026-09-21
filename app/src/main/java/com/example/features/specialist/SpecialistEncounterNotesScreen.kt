package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.*
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SpecialistEncounterNotesScreen(
    appointmentId: String,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepo = AppRepositoryLocator.authRepository
    val appointmentRepo = AppRepositoryLocator.appointmentRepository
    val encounterRepo = AppRepositoryLocator.encounterRepository
    val careRepo = AppRepositoryLocator.careRepository
    val session by authRepo.getActiveSession().collectAsState(initial = null)

    var appointment by remember { mutableStateOf<Appointment?>(null) }
    var existingEncounter by remember { mutableStateOf<ClinicalEncounter?>(null) }

    var summaryText by remember { mutableStateOf("") }
    var observationsText by remember { mutableStateOf("") }
    var agreedNextStepsText by remember { mutableStateOf("") }
    var privateNotesText by remember { mutableStateOf("") }
    var carePlanActionInput by remember { mutableStateOf("") }
    var carePlanActions by remember { mutableStateOf<List<String>>(listOf("Practice diaphragmatic breathing 2x daily")) }
    var followUpDate by remember { mutableStateOf("2026-10-06") }

    var isSaving by remember { mutableStateOf(false) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    LaunchedEffect(appointmentId) {
        val apt = appointmentRepo.getAppointmentById(appointmentId)
        appointment = apt

        val encounter = encounterRepo.getEncounterForAppointment(appointmentId)
        if (encounter != null) {
            existingEncounter = encounter
            summaryText = encounter.consultationSummary
            observationsText = encounter.observations
            agreedNextStepsText = encounter.agreedNextSteps
            privateNotesText = encounter.privatePractitionerNotes
            carePlanActions = encounter.carePlanActions
            followUpDate = encounter.followUpDateIso ?: "2026-10-06"
        } else {
            summaryText = "Patient reviewed ongoing symptoms of work-related burnout and intermittent insomnia."
            observationsText = "Patient was cooperative, reflective, and motivated to integrate structured relaxation routines."
            agreedNextStepsText = "1. Maintain 10-minute bedtime breathwork routine.\n2. Journal peak anxiety triggers during workdays."
            privateNotesText = "Patient shows good prognosis. Follow-up recommended in 2 weeks."
        }
    }

    AuraBackground(
        aura = AuraType.Payments,
        secondaryAura = AuraType.Sessions,
        intensity = 1.15f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CalmWhite)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CalmInkNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmWhite.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CalmEmerald)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "HPCZ Encounter Notes • Lusaka",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Display
                Column {
                    Text(
                        text = "Clinical Encounter Notes",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 30.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "HPCZ SOAP documentation & shared patient care plan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 14.sp
                        )
                    )
                }

                // Success banner
                AnimatedVisibility(visible = showSuccessBanner) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CalmSessionsAura,
                        border = BorderStroke(1.dp, CalmEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CalmEmerald)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Encounter notes & patient care plan saved successfully.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmInkNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Appointment Meta Header
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = CalmDiscoveryAura,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "KT",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = OutfitFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = CalmSphereBlue
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Kondwani Tembo",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Text(
                                        text = "${appointment?.serviceName ?: "Clinical Session"} • ${appointment?.dateIso ?: "Today"}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                            AppointmentStatusChip(status = appointment?.status ?: AppointmentStatus.COMPLETED)
                        }
                    }
                }

                // Section 1: Clinical Observations
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "1. Clinical Observations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Practitioner diagnostic observations during the clinical encounter.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = observationsText,
                            onValueChange = { observationsText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmEmerald,
                                unfocusedBorderColor = CalmHairline
                            )
                        )
                    }
                }

                // Section 2: Shared Summary & Care Actions (Visible to Patient)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.4f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = CalmSphereBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. Shared Patient Summary & Plan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy,
                                    fontSize = 17.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "This summary will automatically sync into the patient's Care plan dashboard.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = summaryText,
                            onValueChange = { summaryText = it },
                            label = { Text("Consultation Summary for Patient") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmSphereBlue,
                                unfocusedBorderColor = CalmHairline
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = agreedNextStepsText,
                            onValueChange = { agreedNextStepsText = it },
                            label = { Text("Agreed Next Steps & Homework") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmSphereBlue,
                                unfocusedBorderColor = CalmHairline
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Prescribed Care Plan Action Items:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 12.sp
                            )
                        )

                        carePlanActions.forEachIndexed { index, action ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CalmEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmInkNavy,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = carePlanActionInput,
                                onValueChange = { carePlanActionInput = it },
                                placeholder = { Text("Add new action goal...", fontSize = 12.sp) },
                                shape = CalmLightShapes.Pill,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CalmEmerald,
                                    unfocusedBorderColor = CalmHairline
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (carePlanActionInput.isNotBlank()) {
                                        carePlanActions = carePlanActions + carePlanActionInput.trim()
                                        carePlanActionInput = ""
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Goal", tint = CalmEmerald)
                            }
                        }
                    }
                }

                // Section 3: Strictly Private Practitioner Notes
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHoneyGold.copy(alpha = 0.5f)),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9A6B1F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. Private Clinical Notes (Practitioner Only)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy,
                                    fontSize = 17.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "These notes are strictly private to you and will NEVER be shown to the patient.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = privateNotesText,
                            onValueChange = { privateNotesText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF9A6B1F),
                                unfocusedBorderColor = CalmHairline
                            )
                        )
                    }
                }

                // Section 4: Recommended Follow-Up
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "4. Follow-Up Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = followUpDate,
                            onValueChange = { followUpDate = it },
                            label = { Text("Recommended Follow-Up Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CalmLightShapes.Pill,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmEmerald,
                                unfocusedBorderColor = CalmHairline
                            )
                        )
                    }
                }

                // Save Button (Styled with premium CalmButton component)
                CalmButton(
                    text = "Save Clinical Encounter Notes",
                    onClick = {
                        isSaving = true
                        coroutineScope.launch {
                            val apt = appointment
                            val newEncounter = ClinicalEncounter(
                                encounterId = existingEncounter?.encounterId ?: "enc_${UUID.randomUUID().toString().take(8)}",
                                appointmentId = appointmentId,
                                practitionerId = apt?.practitionerId ?: (session?.practitionerId ?: "doc_chileshe_01"),
                                patientId = apt?.patientId ?: "pat_demo_me",
                                patientDisplayName = apt?.patientName ?: "Kondwani Tembo",
                                dateIso = apt?.dateIso ?: "Today",
                                consultationType = apt?.consultationType ?: ConsultationType.ONLINE,
                                consultationSummary = summaryText,
                                observations = observationsText,
                                agreedNextSteps = agreedNextStepsText,
                                followUpDateIso = followUpDate,
                                carePlanActions = carePlanActions,
                                referralNote = null,
                                privatePractitionerNotes = privateNotesText,
                                sharedPatientSummary = summaryText,
                                isCompleted = true
                            )
                            encounterRepo.saveEncounter(newEncounter)

                            // Also sync new goals to the patient's care repository so they immediately see it
                            val doctorName = apt?.practitionerName ?: "Dr. Mutale Chileshe"
                            carePlanActions.forEach { actionTitle ->
                                careRepo.addCareGoal(
                                    title = actionTitle,
                                    category = "Specialist Recommendation",
                                    targetDescription = "Prescribed by $doctorName during clinical consultation"
                                )
                            }

                            // Mark appointment completed
                            val targetApt = apt
                            if (targetApt != null) {
                                appointmentRepo.updateAppointmentStatus(targetApt.id, AppointmentStatus.COMPLETED)
                            }

                            isSaving = false
                            showSuccessBanner = true
                        }
                    },
                    variant = CalmButtonVariant.Primary,
                    icon = Icons.Default.Save,
                    isLoading = isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_save_encounter_notes")
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
