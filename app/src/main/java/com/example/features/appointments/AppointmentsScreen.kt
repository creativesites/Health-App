package com.example.features.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.core.design.*
import com.example.core.model.Appointment
import com.example.core.model.AppointmentStatus
import com.example.core.model.ConsultationType
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel,
    onJoinConsultation: (appointmentId: String) -> Unit,
    onMessagePractitioner: (conversationId: String, name: String) -> Unit,
    onNavigateToDiscovery: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredList = remember(uiState.allAppointments, uiState.selectedTab) {
        when (uiState.selectedTab) {
            AppointmentTab.UPCOMING -> uiState.allAppointments.filter {
                it.status == AppointmentStatus.CONFIRMED ||
                it.status == AppointmentStatus.PENDING_PAYMENT ||
                it.status == AppointmentStatus.RESCHEDULED ||
                it.status == AppointmentStatus.IN_PROGRESS
            }
            AppointmentTab.PAST -> uiState.allAppointments.filter {
                it.status == AppointmentStatus.COMPLETED || it.status == AppointmentStatus.NO_SHOW
            }
            AppointmentTab.CANCELLED -> uiState.allAppointments.filter {
                it.status == AppointmentStatus.CANCELLED || it.status == AppointmentStatus.REFUNDED
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionFeedbackMessage) {
        uiState.actionFeedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    AuraBackground(aura = AuraType.Sessions) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = "Appointments",
                    subtitle = "Your sessions & consultation history"
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Segmented Pill Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppointmentTab.values().forEach { tab ->
                        val isSelected = uiState.selectedTab == tab
                        Surface(
                            onClick = { viewModel.onTabSelected(tab) },
                            shape = CalmLightShapes.Pill,
                            color = if (isSelected) CalmInkNavy else CalmWhite,
                            border = BorderStroke(1.dp, if (isSelected) CalmInkNavy else CalmHairline),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (tab) {
                                        AppointmentTab.UPCOMING -> "Upcoming"
                                        AppointmentTab.PAST -> "Past"
                                        AppointmentTab.CANCELLED -> "Cancelled"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) CalmWhite else CalmInkNavy,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    val (title, msg) = when (uiState.selectedTab) {
                        AppointmentTab.UPCOMING -> Pair("No upcoming sessions", "Explore verified Zambian psychologists, doctors, and specialists to book a session.")
                        AppointmentTab.PAST -> Pair("No past sessions", "Your completed consultation logs and notes will appear here.")
                        AppointmentTab.CANCELLED -> Pair("No cancelled sessions", "You have no cancelled appointments.")
                    }
                    CalmEmptyState(
                        title = title,
                        message = msg,
                        actionText = if (uiState.selectedTab == AppointmentTab.UPCOMING) "Find a Practitioner" else null,
                        onAction = onNavigateToDiscovery,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList) { appointment ->
                            AppointmentItemCard(
                                appointment = appointment,
                                onJoin = { onJoinConsultation(appointment.id) },
                                onMessage = { onMessagePractitioner("conv_${appointment.practitionerId}", appointment.practitionerName) },
                                onReschedule = { viewModel.rescheduleAppointment(appointment.id) },
                                onCancel = { viewModel.cancelAppointment(appointment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItemCard(
    appointment: Appointment,
    onJoin: () -> Unit,
    onMessage: () -> Unit,
    onReschedule: () -> Unit,
    onCancel: () -> Unit
) {
    var showEncounterDialog by remember { mutableStateOf(false) }
    var encounterSummary by remember { mutableStateOf<com.example.core.model.ClinicalEncounter?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (showEncounterDialog && encounterSummary != null) {
        val enc = encounterSummary!!
        AlertDialog(
            onDismissRequest = { showEncounterDialog = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = CalmWhite,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CalmSessionsAura,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = CalmEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Doctor's Encounter Summary",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "${appointment.practitionerTitle} ${appointment.practitionerName} • HPCZ Verified",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CalmMistSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Clinical Assessment & Observations",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmSlate,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = enc.observations.ifBlank { enc.consultationSummary.ifBlank { "Patient engaged in structured therapeutic consultation." } },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmInkNavy,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }

                    if (enc.carePlanActions.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CalmDiscoveryAura.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Prescribed Care Plan & Actions",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmSphereBlue,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                enc.carePlanActions.forEach { action ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CalmEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = action,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmInkNavy,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!enc.followUpDateIso.isNullOrBlank()) {
                        Text(
                            text = "Next Follow-up Recommended: ${enc.followUpDateIso}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = CalmEmerald,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEncounterDialog = false },
                    shape = CalmLightShapes.Pill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CalmInkNavy,
                        contentColor = CalmWhite
                    )
                ) {
                    Text("Got It", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFontFamily))
                }
            }
        )
    }

    CalmCard(
        shape = CalmLightShapes.Prominent,
        backgroundColor = CalmWhite,
        modifier = Modifier.testTag("appointment_card_${appointment.id}")
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.serviceName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Text(
                    text = "${appointment.practitionerTitle} ${appointment.practitionerName}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = CalmSphereBlue
                    )
                )
            }
            AppointmentStatusChip(status = appointment.status)
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = CalmHairline)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = CalmSphereBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${appointment.dateIso} • ${appointment.timeSlotLabel}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmInkNavy
                    )
                )
            }
            ConsultationTypeChip(type = appointment.consultationType)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = CalmSlate,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = appointment.locationDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSlate
                )
            )
        }

        if (!appointment.intakeNotes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reason: ${appointment.intakeNotes}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSoftSlate
                )
            )
        }

        // Action buttons
        if (appointment.status == AppointmentStatus.CONFIRMED || appointment.status == AppointmentStatus.RESCHEDULED) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                if (appointment.consultationType == ConsultationType.ONLINE) {
                    CalmButton(
                        text = "Join Room",
                        icon = Icons.Outlined.Videocam,
                        onClick = onJoin,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_join_active_consultation")
                    )
                }

                CalmButton(
                    text = "Message",
                    icon = Icons.Outlined.ChatBubbleOutline,
                    variant = CalmButtonVariant.Secondary,
                    onClick = onMessage,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onReschedule) {
                    Text(
                        "Reschedule",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate
                        )
                    )
                }
                TextButton(onClick = onCancel) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmCrisisCoral
                        )
                    )
                }
            }
        } else if (appointment.status == AppointmentStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val enc = com.example.core.repository.mock.AppRepositoryLocator.encounterRepository
                                .getEncounterForAppointment(appointment.id)
                            encounterSummary = enc ?: com.example.core.model.ClinicalEncounter(
                                encounterId = "enc_default",
                                appointmentId = appointment.id,
                                practitionerId = appointment.practitionerId,
                                patientId = appointment.patientId,
                                patientDisplayName = "Kondwani Tembo",
                                dateIso = appointment.dateIso,
                                consultationType = appointment.consultationType,
                                consultationSummary = "Comprehensive consultation successfully concluded.",
                                observations = "Patient showed positive receptiveness to therapeutic framework and lifestyle adjustments.",
                                agreedNextSteps = "Continue daily 4-4-6 breathing exercise and maintain sleep routine.",
                                followUpDateIso = "2 weeks",
                                carePlanActions = listOf("Daily 4-4-6 breathing exercise", "15-minute screen-free wind down"),
                                isCompleted = true
                            )
                            showEncounterDialog = true
                        }
                    },
                    shape = CalmLightShapes.Pill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CalmSessionsAura,
                        contentColor = CalmEmerald
                    ),
                    modifier = Modifier.weight(1.2f).height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Doctor's Summary",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp
                        )
                    )
                }

                OutlinedButton(
                    onClick = onMessage,
                    shape = CalmLightShapes.Pill,
                    border = BorderStroke(1.dp, CalmHairline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CalmInkNavy
                    ),
                    modifier = Modifier.weight(0.9f).height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = CalmSlate
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Message",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontSize = 12.5.sp
                        )
                    )
                }
            }
        }
    }
}
