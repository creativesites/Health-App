package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
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
    var isChatOpen by remember { mutableStateOf(false) }
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
            // High-fidelity Patient video viewport (Central Main Area)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 70.dp, bottom = 100.dp) // Leave space for top and bottom bars
                    .clip(CalmLightShapes.Prominent)
                    .background(Color(0xFF0F1C2E)),
                contentAlignment = Alignment.Center
            ) {
                if (isVideoMuted) {
                    // Muted placeholder state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TheSphere(size = 80.dp, isBreathing = true)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Patient video stream disabled",
                            style = MaterialTheme.typography.bodyMedium.copy(color = CalmSoftSlate)
                        )
                    }
                } else {
                    // Simulated Patient Camera Feed
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFF1E355E), Color(0xFF0F1C2E))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = CalmWhite.copy(alpha = 0.15f),
                                modifier = Modifier.size(110.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Kondwani Tembo (Patient Feed)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    color = CalmWhite.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }

                // Medical telemetry overlay (Simulating live practitioner metrics!)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFF264366))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = null,
                                tint = CalmCrisisCoral,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "72 bpm",
                                color = CalmWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFF264366))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeviceThermostat,
                                contentDescription = null,
                                tint = CalmHoneyGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Temp: 36.6 °C",
                                color = CalmWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Local Specialist PIP Window (Dr. Chileshe's camera feed)
                val isViewerChileshe = true
                val docImage = if (isViewerChileshe) R.drawable.doc_mwansa_chileshe else R.drawable.doc_thandiwe_zulu
                Surface(
                    shape = CalmLightShapes.Standard,
                    color = Color(0xFF1E355E),
                    border = BorderStroke(1.5.dp, CalmEmerald),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(width = 100.dp, height = 135.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = docImage),
                            contentDescription = "My Camera Feed",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Text(
                            text = "You (REC)",
                            color = CalmWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                        )
                    }
                }
            }

            // Top Status Bar: Call duration & connection indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Info Badge
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = Color(0xFF13253D).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color(0xFF243C5E))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CalmEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Room • $durationFormatted",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Encryption pill
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = Color(0xFF13253D).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color(0xFF243C5E))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Encrypted",
                            tint = CalmSessionsAura,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HPCZ Secure",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSessionsAura,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Controls Bar (Overlay at bottom)
            Surface(
                shape = CalmLightShapes.Pill,
                color = CalmInkNavy.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, CalmWhite.copy(alpha = 0.2f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
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
                            onClick = { isChatOpen = !isChatOpen },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isChatOpen) CalmSphereBlue else CalmWhite.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Open Chat",
                                tint = CalmWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        // End Consultation CTA
                        CalmButton(
                            text = "Conclude",
                            onClick = { showEndDialog = true },
                            variant = CalmButtonVariant.Crisis,
                            icon = Icons.Default.CallEnd,
                            modifier = Modifier.testTag("btn_specialist_end_call")
                        )
                        }
                        }
                        }

                        // Integrated Chat Overlay
                        AnimatedVisibility(
                visible = isChatOpen,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 }) + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it / 2 }) + androidx.compose.animation.fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp) // Sits above the dock
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    shape = CalmLightShapes.Prominent,
                    color = Color(0xFF162840).copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, Color(0xFF385882)),
                    shadowElevation = 24.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Column {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "In-Call Messages",
                                style = MaterialTheme.typography.titleSmall.copy(color = CalmWhite)
                            )
                            IconButton(
                                onClick = { isChatOpen = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close chat", tint = CalmSoftSlate)
                            }
                        }
                        HorizontalDivider(color = Color(0xFF243C5E))
                        
                        // Messages Area (Mocked for in-call)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Mock message from patient
                            Row(verticalAlignment = Alignment.Bottom) {
                                Surface(
                                    shape = CircleShape,
                                    color = CalmSphereBlue,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("K", color = CalmWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                                    color = Color(0xFF243C5E)
                                ) {
                                    Text(
                                        text = "Yes, I usually wake up around 3 AM and struggle to fall back asleep for about an hour.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = CalmWhite),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                        
                        // Input Area
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = { Text("Type a message...", color = CalmSoftSlate, fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = CalmLightShapes.Pill,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF0C1B2E),
                                    unfocusedContainerColor = Color(0xFF0C1B2E),
                                    focusedBorderColor = CalmSphereBlue,
                                    unfocusedBorderColor = Color(0xFF243C5E),
                                    focusedTextColor = CalmWhite,
                                    unfocusedTextColor = CalmWhite
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(CalmSphereBlue, CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = CalmWhite, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
