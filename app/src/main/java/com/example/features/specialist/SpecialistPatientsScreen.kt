package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.core.model.SpecialistPatientSummary
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SpecialistPatientsScreen(
    onOpenPatientChat: (patientId: String, patientName: String) -> Unit
) {
    val authRepo = AppRepositoryLocator.authRepository
    val specialistRepo = AppRepositoryLocator.specialistRepository
    val session by authRepo.getActiveSession().collectAsState(initial = null)
    val practitionerId = session?.practitionerId ?: "doc_chileshe_01"

    var patients by remember { mutableStateOf<List<SpecialistPatientSummary>>(emptyList()) }
    var selectedPatient by remember { mutableStateOf<SpecialistPatientSummary?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(practitionerId) {
        specialistRepo.getSpecialistPatients(practitionerId).collectLatest { list ->
            patients = list
        }
    }

    val filteredPatients = patients.filter {
        it.displayName.contains(searchQuery, ignoreCase = true) ||
        it.intakeSummary.contains(searchQuery, ignoreCase = true) ||
        it.careStatus.contains(searchQuery, ignoreCase = true)
    }

    // Rich Multi-Tab Clinical Dossier Dialog
    if (selectedPatient != null) {
        val patient = selectedPatient!!
        var dossierTab by remember { mutableIntStateOf(0) } // 0: Intake & Bio, 1: Care Goals, 2: History

        AlertDialog(
            onDismissRequest = { selectedPatient = null },
            shape = RoundedCornerShape(26.dp),
            containerColor = CalmWhite,
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = CalmDiscoveryAura,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = patient.displayName.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmSphereBlue,
                                            fontSize = 18.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = patient.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmInkNavy,
                                        fontSize = 19.sp
                                    )
                                )
                                Text(
                                    text = "${patient.gender} • ${patient.age} yrs • ${patient.city}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = CalmLightShapes.Pill,
                            color = CalmSessionsAura
                        ) {
                            Text(
                                text = patient.careStatus,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = CalmEmerald,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Intake & Context", "Prescribed Goals", "Consultation Log").forEachIndexed { idx, title ->
                            val isSelected = dossierTab == idx
                            Surface(
                                onClick = { dossierTab = idx },
                                shape = CalmLightShapes.Pill,
                                color = if (isSelected) CalmInkNavy else CalmMistSurface,
                                modifier = Modifier.weight(1f).height(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) CalmWhite else CalmSlate,
                                            fontSize = 10.5.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    when (dossierTab) {
                        0 -> {
                            // Intake & Context Tab
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CalmMistSurface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Primary Presenting Concern:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmSlate,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = patient.intakeSummary,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmInkNavy,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Contact Verified", style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate, fontSize = 11.sp))
                                    Text(patient.contactNumberMasked, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = CalmInkNavy, fontSize = 13.sp))
                                }
                                Column {
                                    Text("Total Sessions", style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate, fontSize = 11.sp))
                                    Text("${patient.totalSessions} Sessions", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = CalmInkNavy, fontSize = 13.sp))
                                }
                            }
                        }
                        1 -> {
                            // Prescribed Goals Tab
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = CalmDiscoveryAura.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CalmEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Sleep Consistency (10:30 PM)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 12.5.sp))
                                            Text("Progress: 70% • 5 nights/week", style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate, fontSize = 11.sp))
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = CalmDiscoveryAura.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CalmEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Grounding Breathwork (4-4-6)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 12.5.sp))
                                            Text("Progress: 100% • Daily Morning box rhythm", style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate, fontSize = 11.sp))
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            // Consultation Log Tab
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Last Completed Session: ${patient.lastSessionDate}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 12.sp))
                                Text("Next Booked: ${patient.nextSessionDate ?: "None"}", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, color = CalmInkNavy, fontSize = 13.sp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CalmMistSurface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "HPCZ Compliance: Patient identity verified via National ID and pre-consultation tele-triage checklist completed.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFontFamily, color = CalmSlate, fontSize = 11.5.sp),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                CalmButton(
                    text = "Direct Message",
                    onClick = {
                        val p = selectedPatient ?: return@CalmButton
                        selectedPatient = null
                        onOpenPatientChat(p.patientId, p.displayName)
                    },
                    variant = CalmButtonVariant.Primary,
                    icon = Icons.Outlined.ChatBubbleOutline,
                    modifier = Modifier.height(40.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { selectedPatient = null }) {
                    Text("Close Dossier", color = CalmSlate, style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily))
                }
            }
        )
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
                                text = "Patients • Lusaka Practice",
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
            ) {
                // Header Display
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    Text(
                        text = "Patient Care Roster",
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
                        text = "Clinical profiles, intake notes & follow-up care records",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Serene Search Bar
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = CalmSlate,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search by name, intake condition...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSoftSlate,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmInkNavy,
                                    fontSize = 14.sp
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("patient_search_input")
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = CalmSlate,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Patients count & summary pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredPatients.size} Active Patients",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy
                        )
                    )

                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmSessionsAura
                    ) {
                        Text(
                            text = "HPCZ ROSTER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = CalmEmerald,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredPatients.isEmpty()) {
                    CalmEmptyState(
                        title = "No patients found",
                        message = "Try searching for a different name or clear the search filter.",
                        actionText = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredPatients) { patient ->
                            PatientRosterCard(
                                patient = patient,
                                onOpenDetail = { selectedPatient = patient },
                                onOpenChat = { onOpenPatientChat(patient.patientId, patient.displayName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientRosterCard(
    patient: SpecialistPatientSummary,
    onOpenDetail: () -> Unit,
    onOpenChat: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = CalmDiscoveryAura,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = patient.displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = CalmSphereBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = patient.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.5.sp
                            ),
                            color = CalmInkNavy
                        )
                        Text(
                            text = "${patient.gender} • ${patient.age} yrs • ${patient.city}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmMistSurface
                ) {
                    Text(
                        text = patient.careStatus,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmEmerald,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = patient.intakeSummary,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmInkNavy.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${patient.totalSessions} completed sessions",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate,
                        fontSize = 11.5.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CalmButton(
                        text = "Intake File",
                        onClick = onOpenDetail,
                        variant = CalmButtonVariant.Secondary,
                        modifier = Modifier.height(36.dp)
                    )

                    CalmButton(
                        text = "Message",
                        onClick = onOpenChat,
                        variant = CalmButtonVariant.Primary,
                        icon = Icons.Outlined.ChatBubbleOutline,
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }
    }
}
