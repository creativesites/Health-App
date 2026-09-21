package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.core.model.Appointment
import com.example.core.model.AppointmentStatus
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpecialistConsultationScreen(
    appointmentId: String,
    onEndConsultation: (appointmentId: String) -> Unit,
    onOpenEncounterNotes: (appointmentId: String) -> Unit,
    onOpenChat: (conversationId: String, participantName: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val appointmentRepo = AppRepositoryLocator.appointmentRepository
    var appointment by remember { mutableStateOf<Appointment?>(null) }

    var isMicMuted by remember { mutableStateOf(false) }
    var isVideoMuted by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(184) } // Demo start at 3:04
    var showEndDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appointmentId) {
        val apt = appointmentRepo.getAppointmentById(appointmentId)
        appointment = apt
    }

    // Call timer simulation
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val durationFormatted = String.format("%02d:%02d", minutes, seconds)

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            shape = CalmLightShapes.Standard,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Conclude Clinical Session?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        color = CalmInkNavy
                    )
                )
            },
            text = {
                Text(
                    text = "This will mark the session as completed and transition you directly to the clinical encounter notes workspace.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEndDialog = false
                        coroutineScope.launch {
                            val apt = appointment
                            if (apt != null) {
                                appointmentRepo.updateAppointmentStatus(apt.id, AppointmentStatus.COMPLETED)
                            }
                            onEndConsultation(appointmentId)
                        }
                    }
                ) {
                    Text("Conclude & Write Notes", color = CalmEmerald, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text("Stay in Call", color = CalmSlate)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF0C1B2E) // Deep peaceful midnight navy for specialist teleconsultation
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main Virtual Room Viewport
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info Bar
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmInkNavy.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, CalmWhite.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CalmEmerald,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Clinical Room • $durationFormatted",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmWhite,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            shape = CalmLightShapes.Pill,
                            color = CalmWhite.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Encrypted",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSessionsAura,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Patient Video Representation / Visual Sanctuary
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CalmSphereBlue.copy(alpha = 0.12f),
                        border = BorderStroke(2.dp, CalmSphereBlue.copy(alpha = 0.35f)),
                        modifier = Modifier.size(130.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            TheSphere(size = 90.dp, isBreathing = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Patient: ${appointment?.patientName ?: "Kondwani Tembo"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmWhite,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Consultation: ${appointment?.serviceName ?: "Clinical Session"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    )

                    if (!appointment?.intakeNotes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = CalmLightShapes.Standard,
                            color = CalmWhite.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, CalmWhite.copy(alpha = 0.12f)),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Intake Reference:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSessionsAura,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = appointment?.intakeNotes ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmWhite,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Controls Bar
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmInkNavy.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, CalmWhite.copy(alpha = 0.2f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute Mic
                        IconButton(
                            onClick = { isMicMuted = !isMicMuted },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isMicMuted) CalmCrisisCoral.copy(alpha = 0.2f) else CalmWhite.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Toggle Mic",
                                tint = if (isMicMuted) CalmCrisisCoral else CalmWhite
                            )
                        }

                        // Video Toggle
                        IconButton(
                            onClick = { isVideoMuted = !isVideoMuted },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isVideoMuted) CalmCrisisCoral.copy(alpha = 0.2f) else CalmWhite.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = if (isVideoMuted) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Toggle Video",
                                tint = if (isVideoMuted) CalmCrisisCoral else CalmWhite
                            )
                        }

                        // Clinical Notes Quick Access
                        IconButton(
                            onClick = { onOpenEncounterNotes(appointmentId) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CalmSphereBlue.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EditNote,
                                contentDescription = "Open Notes",
                                tint = CalmSphereBlue
                            )
                        }

                        // In-call Chat
                        IconButton(
                            onClick = {
                                onOpenChat(
                                    "conv_${appointment?.patientId ?: "pat_demo_me"}",
                                    appointment?.patientName ?: "Kondwani Tembo"
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CalmWhite.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Open Chat",
                                tint = CalmWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // End Consultation CTA
                        Button(
                            onClick = { showEndDialog = true },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CalmCrisisCoral,
                                contentColor = CalmWhite
                            ),
                            modifier = Modifier.testTag("btn_specialist_end_call")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Conclude",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
