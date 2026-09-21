package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.*
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
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

    // Unified SOAP State
    var subjectiveText by remember { mutableStateOf("") }
    var objectiveText by remember { mutableStateOf("") }
    var assessmentText by remember { mutableStateOf("") }
    var planText by remember { mutableStateOf("") }

    // ICD-10 Diagnosis Codes
    var icd10Input by remember { mutableStateOf("") }
    var icd10Codes by remember { mutableStateOf<List<String>>(emptyList()) }

    // E-Prescriptions State
    var rxMedName by remember { mutableStateOf("") }
    var rxDosage by remember { mutableStateOf("") }
    var rxFrequency by remember { mutableStateOf("") }
    var rxDuration by remember { mutableStateOf("14") }
    var prescriptions by remember { mutableStateOf<List<Prescription>>(emptyList()) }
    var showRxForm by remember { mutableStateOf(false) }

    // Lab Orders State
    var labTestName by remember { mutableStateOf("") }
    var labRationale by remember { mutableStateOf("") }
    var labOrders by remember { mutableStateOf<List<LabOrder>>(emptyList()) }
    var showLabForm by remember { mutableStateOf(false) }

    // Legacy fields mapped for backwards compatibility with the Patient Summary dashboard
    var summaryText by remember { mutableStateOf("") }
    var observationsText by remember { mutableStateOf("") }
    var agreedNextStepsText by remember { mutableStateOf("") }
    var privateNotesText by remember { mutableStateOf("") }
    var carePlanActionInput by remember { mutableStateOf("") }
    var carePlanActions by remember { mutableStateOf<List<String>>(listOf("Practice diaphragmatic breathing 2x daily")) }
    var followUpDate by remember { mutableStateOf("2026-10-06") }

    // AI Scribe & Transcription State
    var dictatingField by remember { mutableStateOf<String?>(null) } // "subjective", "objective", etc.
    var isDictating by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    // Load initial values or set realistic defaults
    LaunchedEffect(appointmentId) {
        val apt = appointmentRepo.getAppointmentById(appointmentId)
        appointment = apt

        val encounter = encounterRepo.getEncounterForAppointment(appointmentId)
        if (encounter != null) {
            existingEncounter = encounter
            subjectiveText = encounter.subjectiveText.ifBlank { "Patient reports work-related stress and subsequent initial insomnia." }
            objectiveText = encounter.objectiveText.ifBlank { "Mental Status: Alert, cooperative, slight psychomotor agitation in hands." }
            assessmentText = encounter.assessmentText.ifBlank { "Workplace burnout, mild chronic fatigue, and situational insomnia." }
            planText = encounter.planText.ifBlank { "CBT-i techniques, cognitive boundaries, and diaphragmatic breathing." }
            icd10Codes = encounter.icd10Codes
            prescriptions = encounter.prescriptions
            labOrders = encounter.labOrders
            summaryText = encounter.consultationSummary
            observationsText = encounter.observations
            agreedNextStepsText = encounter.agreedNextSteps
            privateNotesText = encounter.privatePractitionerNotes
            carePlanActions = encounter.carePlanActions
            followUpDate = encounter.followUpDateIso ?: "2026-10-06"
        } else {
            // Seed defaults for the V1 showcase
            subjectiveText = "Patient reviewed ongoing symptoms of work-related burnout, feeling overwhelmed by deadlines, and intermittent initial insomnia."
            objectiveText = "Mental Status Exam: Patient is groomed, alert, and cooperative. Speech is coherent and fluent. Moderate physical fidgeting observed, congruent with anxious affect."
            assessmentText = "Workplace burnout, situational anxiety, and chronic insomnia secondary to stress."
            planText = "CBT-i breathing, work boundaries, trigger tracking, and support groups."
            icd10Codes = listOf("F41.1 (Generalized Anxiety)", "F51.01 (Primary Insomnia)")
            summaryText = "Patient reviewed ongoing symptoms of work-related burnout and intermittent insomnia."
            observationsText = "Patient was cooperative, reflective, and motivated to integrate structured relaxation routines."
            agreedNextStepsText = "1. Maintain 10-minute bedtime breathwork routine.\n2. Journal peak anxiety triggers during workdays."
            privateNotesText = "Patient shows good prognosis. Follow-up recommended in 2 weeks."
            prescriptions = listOf(
                Prescription(
                    id = "rx_01",
                    medicationName = "Melatonin",
                    dosage = "5mg",
                    frequency = "1 tablet once daily before bedtime",
                    durationDays = 14,
                    instructions = "Avoid screen exposure after consuming"
                )
            )
            labOrders = emptyList()
        }
    }

    // Typewriter transcription simulation
    fun simulateDictation(field: String, narrativeText: String) {
        coroutineScope.launch {
            dictatingField = field
            isDictating = true
            
            // Typewriter effect
            val words = narrativeText.split(" ")
            var combined = ""
            for (word in words) {
                delay(80) // Paced simulation
                combined += if (combined.isEmpty()) word else " $word"
                when (field) {
                    "subjective" -> subjectiveText = combined
                    "objective" -> objectiveText = combined
                    "assessment" -> assessmentText = combined
                    "plan" -> planText = combined
                    "private" -> privateNotesText = combined
                }
            }
            
            isDictating = false
            dictatingField = null
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
                                text = "HPCZ SOAP System • Verified Portal",
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
                        text = "Clinical SOAP Notes",
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
                        text = "Professional clinical documentation, prescriptions & diagnostic testing",
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
                                text = "Encounter notes, prescriptions & care plan synced successfully.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmInkNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Smart Templates Quick Action Bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CalmSessionsAura,
                    border = BorderStroke(1.dp, CalmEmerald.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null, tint = CalmEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Smart Clinical Templates",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, color = CalmInkNavy)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // CBT Insomnia Template
                            Surface(
                                onClick = {
                                    subjectiveText = "Patient reports feeling severe workplace burnout with significant chest tightness when checking emails. Struggling with initial insomnia, waking up anxious."
                                    objectiveText = "MSE: Patient alert, oriented. Speech is pressured. Affect is anxious. Noticeable physical tremor in hands. Insight intact."
                                    assessmentText = "Work-related anxiety disorder with secondary chronic psychophysiological insomnia."
                                    planText = "Enroll patient in 6-session CBT-i protocol. Establish structural work boundary (no email after 18:00). Integrate bedtime box breathing."
                                    icd10Codes = listOf("F41.1 (Generalized Anxiety)", "F51.01 (Primary Insomnia)")
                                    carePlanActions = listOf("Perform 4-4-6 breathing before bed", "Write trigger journal at 17:00", "No screens after 21:30")
                                },
                                shape = CalmLightShapes.Pill,
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("CBT Insomnia", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = CalmInkNavy)
                                }
                            }

                            // General Medicine Checkup Template
                            Surface(
                                onClick = {
                                    subjectiveText = "Patient presents for routine physical evaluation. Reports mild fatigue and physical inactivity. Denies acute pains."
                                    objectiveText = "Vitals: BP 120/80 mmHg, Pulse 72 bpm, Temp 36.7C. Heart/Lungs: Clear on auscultation. Normal abdomen."
                                    assessmentText = "Healthy clinical baseline with mild chronic sedentary fatigue."
                                    planText = "Increase daily light cardio to 30 mins, integrate hydration tracking, monitor vitals and follow up in 1 month."
                                    icd10Codes = listOf("Z00.00 (General Adult Exam)", "R53.83 (Other Fatigue)")
                                    carePlanActions = listOf("Walk 30 minutes daily", "Drink 2.5L of local mineral water", "Limit caffeine after 14:00")
                                },
                                shape = CalmLightShapes.Pill,
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Routine GP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = CalmInkNavy)
                                }
                            }

                            // Pediatrics / Dental Template
                            Surface(
                                onClick = {
                                    subjectiveText = "Patient presenting for routine developmental checkup. Parent reports excellent energy, balanced diet, and normal sleep milestones."
                                    objectiveText = "MSE/Pediatric physical: Normal gross and fine motor milestones, height/weight at 75th percentile. Clear clear speech."
                                    assessmentText = "Healthy childhood developmental milestones."
                                    planText = "Maintain developmental toys, schedule routine immunizations, recommend pediatric dental evaluation."
                                    icd10Codes = listOf("Z00.129 (Routine Child Health Exam)")
                                    carePlanActions = listOf("Read storybook together daily", "Ensure toddler developmental milestones check", "Brush teeth twice daily")
                                },
                                shape = CalmLightShapes.Pill,
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Pediatric Exam", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = CalmInkNavy)
                                }
                            }
                        }
                    }
                }

                // Section 1: Subjective & Objective Textareas (SO)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "S • Subjective Narrative",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                            )
                            
                            // AI Scribe Floating microphone simulation
                            val dictatingColor by animateColorAsState(
                                targetValue = if (dictatingField == "subjective") CalmCrisisCoral else CalmSlate,
                                animationSpec = tween(durationMillis = 400)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(CalmLightShapes.Pill)
                                    .clickable {
                                        simulateDictation(
                                            field = "subjective",
                                            narrativeText = "Patient reports increased work anxiety, sleep onset insomnia, and tension headaches when reviewing quarterly reports. Finds it hard to decouple at night."
                                        )
                                    }
                                    .background(if (dictatingField == "subjective") CalmCrisisCoralSoft else CalmMistSurface)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = if (dictatingField == "subjective") Icons.Default.MicNone else Icons.Default.Mic,
                                    contentDescription = "AI Scribe",
                                    tint = dictatingColor,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .scale(if (dictatingField == "subjective") 1.25f else 1.0f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (dictatingField == "subjective") "Listening..." else "AI Scribe",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold),
                                    color = dictatingColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Patient symptoms, emotional reports, and timeline in their own words.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.5.sp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = subjectiveText,
                            onValueChange = { subjectiveText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmEmerald,
                                unfocusedBorderColor = CalmHairline
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "O • Objective Findings",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(CalmLightShapes.Pill)
                                    .clickable {
                                        simulateDictation(
                                            field = "objective",
                                            narrativeText = "Mental Status: Clean-groomed, normal speech patterns. Affect anxious but fully congruent. Active fidgeting, hand wringing. Reflexes intact."
                                        )
                                    }
                                    .background(if (dictatingField == "objective") CalmCrisisCoralSoft else CalmMistSurface)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = if (dictatingField == "objective") Icons.Default.MicNone else Icons.Default.Mic,
                                    contentDescription = "AI Scribe",
                                    tint = if (dictatingField == "objective") CalmCrisisCoral else CalmSlate,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (dictatingField == "objective") "Listening..." else "AI Scribe",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold),
                                    color = if (dictatingField == "objective") CalmCrisisCoral else CalmSlate
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vitals, diagnostic measurements, clinical exam, or Mental Status observations.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.5.sp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = objectiveText,
                            onValueChange = { objectiveText = it },
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

                // Section 2: Assessment & ICD-10 Coding (A)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "A • Assessment & Diagnoses",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Diagnostic evaluations & standard clinical classifications.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.5.sp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = assessmentText,
                            onValueChange = { assessmentText = it },
                            placeholder = { Text("Primary Diagnosis narrative...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmEmerald,
                                unfocusedBorderColor = CalmHairline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ICD-10 Search & Tags Section
                        Text(
                            text = "ICD-10 Diagnostic Codes:",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Render coded chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            icd10Codes.forEach { code ->
                                Surface(
                                    shape = CalmLightShapes.Pill,
                                    color = CalmSessionsAura,
                                    border = BorderStroke(1.dp, CalmEmerald.copy(alpha = 0.3f)),
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(code, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp), color = CalmEmerald)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete",
                                            tint = CalmEmerald,
                                            modifier = Modifier
                                                .size(13.dp)
                                                .clickable { icd10Codes = icd10Codes - code }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = icd10Input,
                                onValueChange = { icd10Input = it },
                                placeholder = { Text("Add diagnosis code (e.g. F41.1)...", fontSize = 12.sp) },
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
                                    if (icd10Input.isNotBlank()) {
                                        icd10Codes = icd10Codes + icd10Input.trim()
                                        icd10Input = ""
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Code", tint = CalmEmerald)
                            }
                        }
                    }
                }

                // Section 3: Plan & Care Actions (P) — Including Prescriptions & Labs
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.4f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Assignment, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "P • Treatment Plan & Orders",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Prescribed therapeutics, medication (E-Rx), lab orders, and patient summaries.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.5.sp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = planText,
                            onValueChange = { planText = it },
                            label = { Text("Plan Narrative / Interventions") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CalmSphereBlue,
                                unfocusedBorderColor = CalmHairline
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // ==========================================
                        // PRESTIGIOUS E-PRESCRIPTION SECTION (Rx)
                        // ==========================================
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.Medication, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "E-Prescriptions (Rx)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, color = CalmInkNavy)
                                )
                            }
                            TextButton(onClick = { showRxForm = !showRxForm }) {
                                Text(if (showRxForm) "Hide Form" else "+ Add Medication", color = CalmSphereBlue, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        // Prescribed medication list
                        if (prescriptions.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                prescriptions.forEach { rx ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = CalmDiscoveryAura.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.2f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("${rx.medicationName} ${rx.dosage}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CalmInkNavy))
                                                    Text("${rx.frequency} • ${rx.durationDays} Days", style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate, fontSize = 11.5.sp))
                                                    if (!rx.instructions.isNullOrBlank()) {
                                                        Text("Note: ${rx.instructions}", style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic, color = CalmSlate, fontSize = 10.5.sp))
                                                    }
                                                }
                                            }
                                            IconButton(onClick = { prescriptions = prescriptions.filter { it.id != rx.id } }) {
                                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CalmCrisisCoral, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = showRxForm) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CalmMistSurface,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = rxMedName,
                                        onValueChange = { rxMedName = it },
                                        label = { Text("Medication Name (e.g. Paracetamol)") },
                                        shape = CalmLightShapes.Pill,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = rxDosage,
                                            onValueChange = { rxDosage = it },
                                            label = { Text("Dosage (e.g. 500mg)") },
                                            shape = CalmLightShapes.Pill,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                        )
                                        OutlinedTextField(
                                            value = rxDuration,
                                            onValueChange = { rxDuration = it },
                                            label = { Text("Duration (Days)") },
                                            shape = CalmLightShapes.Pill,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                        )
                                    }
                                    OutlinedTextField(
                                        value = rxFrequency,
                                        onValueChange = { rxFrequency = it },
                                        label = { Text("Frequency (e.g. Twice daily after meals)") },
                                        shape = CalmLightShapes.Pill,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                    )
                                    
                                    CalmButton(
                                        text = "Add Prescription",
                                        onClick = {
                                            if (rxMedName.isNotBlank() && rxDosage.isNotBlank()) {
                                                prescriptions = prescriptions + Prescription(
                                                    id = "rx_${UUID.randomUUID().toString().take(6)}",
                                                    medicationName = rxMedName.trim(),
                                                    dosage = rxDosage.trim(),
                                                    frequency = rxFrequency.trim().ifBlank { "Once daily" },
                                                    durationDays = rxDuration.toIntOrNull() ?: 7
                                                )
                                                rxMedName = ""
                                                rxDosage = ""
                                                rxFrequency = ""
                                                rxDuration = "14"
                                                showRxForm = false
                                            }
                                        },
                                        variant = CalmButtonVariant.Primary,
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // ==========================================
                        // PRESTIGIOUS LAB ORDER SECTION
                        // ==========================================
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.Biotech, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lab Diagnostics",
                                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, color = CalmInkNavy)
                                )
                            }
                            TextButton(onClick = { showLabForm = !showLabForm }) {
                                Text(if (showLabForm) "Hide Form" else "+ Order Test", color = CalmSphereBlue, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        // Prescribed lab list
                        if (labOrders.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                labOrders.forEach { lab ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = CalmDiscoveryAura.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.2f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(lab.testName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CalmInkNavy))
                                                    Text("Rationale: ${lab.rationale}", style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate, fontSize = 11.5.sp))
                                                }
                                            }
                                            IconButton(onClick = { labOrders = labOrders.filter { it.id != lab.id } }) {
                                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CalmCrisisCoral, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = showLabForm) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CalmMistSurface,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = labTestName,
                                        onValueChange = { labTestName = it },
                                        label = { Text("Diagnostic Test Name (e.g. Hemoglobin A1C)") },
                                        shape = CalmLightShapes.Pill,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                    )
                                    OutlinedTextField(
                                        value = labRationale,
                                        onValueChange = { labRationale = it },
                                        label = { Text("Clinical Indication / Rationale") },
                                        shape = CalmLightShapes.Pill,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                                    )
                                    
                                    CalmButton(
                                        text = "Add Lab Order",
                                        onClick = {
                                            if (labTestName.isNotBlank()) {
                                                labOrders = labOrders + LabOrder(
                                                    id = "lab_${UUID.randomUUID().toString().take(6)}",
                                                    testName = labTestName.trim(),
                                                    rationale = labRationale.trim().ifBlank { "Routine surveillance" }
                                                )
                                                labTestName = ""
                                                labRationale = ""
                                                showLabForm = false
                                            }
                                        },
                                        variant = CalmButtonVariant.Primary,
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Legacy compatibility field updates synced beautifully
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Visibility, contentDescription = null, tint = CalmSphereBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shared Patient Summary & Homework",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, color = CalmInkNavy)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = summaryText,
                            onValueChange = { summaryText = it },
                            label = { Text("Consultation Summary for Patient Dashboard") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = agreedNextStepsText,
                            onValueChange = { agreedNextStepsText = it },
                            label = { Text("Agreed Next Steps & Homework") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmSphereBlue, unfocusedBorderColor = CalmHairline)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Prescribed Care Plan Action Items:",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 12.sp)
                        )

                        carePlanActions.forEachIndexed { index, action ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CalmEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmInkNavy, fontSize = 13.sp),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = CalmCrisisCoral,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { carePlanActions = carePlanActions - action }
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
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmEmerald, unfocusedBorderColor = CalmHairline)
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
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF9A6B1F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Private Notes (Practitioner Only)",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "These notes are strictly private to you and will NEVER be shown to the patient.",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.5.sp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = privateNotesText,
                            onValueChange = { privateNotesText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF9A6B1F), unfocusedBorderColor = CalmHairline)
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
                            text = "Recommended Follow-Up",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 17.sp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = followUpDate,
                            onValueChange = { followUpDate = it },
                            label = { Text("Recommended Follow-Up Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CalmLightShapes.Pill,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CalmEmerald, unfocusedBorderColor = CalmHairline)
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
                                isCompleted = true,
                                subjectiveText = subjectiveText,
                                objectiveText = objectiveText,
                                assessmentText = assessmentText,
                                planText = planText,
                                icd10Codes = icd10Codes,
                                prescriptions = prescriptions,
                                labOrders = labOrders
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
