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
import com.example.core.model.*
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class SpecialistScheduleFilter(val label: String) {
    ALL("All Slots"),
    CONFIRMED("Confirmed"),
    REQUESTS("Pending"),
    COMPLETED("Completed")
}

@Composable
fun SpecialistAppointmentsScreen(
    onStartConsultation: (appointmentId: String) -> Unit,
    onWriteEncounterNotes: (appointmentId: String) -> Unit,
    onOpenAvailability: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepo = AppRepositoryLocator.authRepository
    val appointmentRepo = AppRepositoryLocator.appointmentRepository
    val session by authRepo.getActiveSession().collectAsState(initial = null)
    val practitionerId = session?.practitionerId ?: "doc_chileshe_01"

    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf(SpecialistScheduleFilter.ALL) }
    var showConfirmDialogForId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(practitionerId) {
        appointmentRepo.getAppointments().collectLatest { list ->
            appointments = list.filter { it.practitionerId == practitionerId }
        }
    }

    val filteredAppointments = when (selectedFilter) {
        SpecialistScheduleFilter.ALL -> appointments
        SpecialistScheduleFilter.CONFIRMED -> appointments.filter { it.status == AppointmentStatus.CONFIRMED }
        SpecialistScheduleFilter.REQUESTS -> appointments.filter { it.status == AppointmentStatus.PENDING_PAYMENT || it.status == AppointmentStatus.RESCHEDULE_REQUESTED }
        SpecialistScheduleFilter.COMPLETED -> appointments.filter { it.status == AppointmentStatus.COMPLETED }
    }

    if (showConfirmDialogForId != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialogForId = null },
            shape = CalmLightShapes.Standard,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Confirm Consultation Request?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        color = CalmInkNavy
                    )
                )
            },
            text = {
                Text(
                    text = "This will mark the session as confirmed and notify the patient that their slot is reserved.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = showConfirmDialogForId ?: return@TextButton
                        coroutineScope.launch {
                            appointmentRepo.updateAppointmentStatus(id, AppointmentStatus.CONFIRMED)
                            showConfirmDialogForId = null
                        }
                    }
                ) {
                    Text("Confirm Slot", color = CalmEmerald, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialogForId = null }) {
                    Text("Cancel", color = CalmSlate)
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
                    // Left: Connection & Location Pill Chip
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
                                text = "Practice Schedule • Lusaka",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    // Right: Availability Quick Toggle
                    Surface(
                        onClick = onOpenAvailability,
                        shape = CircleShape,
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.EditCalendar,
                                contentDescription = "Edit Availability",
                                tint = CalmInkNavy,
                                modifier = Modifier.size(18.dp)
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
                        text = "Clinical Schedule",
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
                        text = "Consultation queues, patient visits & follow-up care",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecialistScheduleFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = CalmLightShapes.Pill,
                            color = if (isSelected) CalmDarkMatte else CalmWhite,
                            border = BorderStroke(1.dp, if (isSelected) CalmDarkMatte else CalmHairline),
                            shadowElevation = if (isSelected) 2.dp else 0.5.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) CalmWhite else CalmInkNavy,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (filteredAppointments.isEmpty()) {
                    CalmEmptyState(
                        title = "No consultations found",
                        message = "There are no appointments matching the selected filter category.",
                        actionText = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredAppointments) { appointment ->
                            SpecialistScheduleCard(
                                appointment = appointment,
                                onConfirmRequest = { showConfirmDialogForId = appointment.id },
                                onStartConsultation = { onStartConsultation(appointment.id) },
                                onWriteEncounterNotes = { onWriteEncounterNotes(appointment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialistScheduleCard(
    appointment: Appointment,
    onConfirmRequest: () -> Unit,
    onStartConsultation: () -> Unit,
    onWriteEncounterNotes: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ConsultationTypeChip(type = appointment.consultationType)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${appointment.dateIso} • ${appointment.timeSlotLabel} CAT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = CalmInkNavy,
                            fontSize = 13.sp
                        )
                    )
                }

                AppointmentStatusChip(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Patient details row
            val patientInitials = appointment.patientName.trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "PT" }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CalmDiscoveryAura,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = patientInitials,
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
                        text = appointment.patientName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        ),
                        color = CalmInkNavy
                    )
                    Text(
                        text = "${appointment.serviceName} • K${appointment.priceZmw.toInt()} (${appointment.paymentStatus.name.lowercase().replaceFirstChar { it.uppercase() }})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 12.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (appointment.status == AppointmentStatus.PENDING_PAYMENT ||
                    appointment.status == AppointmentStatus.RESCHEDULE_REQUESTED
                ) {
                    CalmButton(
                        text = "Confirm Booking",
                        onClick = onConfirmRequest,
                        variant = CalmButtonVariant.Primary,
                        modifier = Modifier.weight(1f).height(42.dp)
                    )
                } else {
                    CalmButton(
                        text = "Clinical Notes",
                        onClick = onWriteEncounterNotes,
                        variant = CalmButtonVariant.Secondary,
                        icon = Icons.Outlined.EditNote,
                        modifier = Modifier.weight(1f).height(42.dp)
                    )

                    CalmButton(
                        text = "Launch",
                        onClick = onStartConsultation,
                        variant = CalmButtonVariant.Primary,
                        icon = Icons.Outlined.Videocam,
                        modifier = Modifier.weight(1f).height(42.dp)
                    )
                }
            }
        }
    }
}
