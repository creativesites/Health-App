package com.example.features.consultation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.core.design.*
import com.example.ui.theme.*

@Composable
fun ConsultationScreen(
    viewModel: ConsultationViewModel,
    onOpenChat: (conversationId: String, name: String) -> Unit,
    onLeaveConsultation: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val appointment = uiState.appointment

    val minutes = uiState.sessionDurationSeconds / 60
    val seconds = uiState.sessionDurationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    if (uiState.showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLeaveDialog() },
            shape = CalmLightShapes.Prominent,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Leave consultation?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    ),
                    color = CalmInkNavy
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to end your active clinical session with ${appointment?.practitionerName}?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmLeaveCall(onLeave = onLeaveConsultation) }
                ) {
                    Text(
                        "End Session",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CalmCrisisCoral
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissLeaveDialog() }) {
                    Text(
                        "Stay in Call",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = CalmInkNavy
                        )
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF0C1B2E) // Deep peaceful midnight navy for consultation
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Editorial backdrop texture for immersive clinical ambience
            Image(
                painter = painterResource(id = R.drawable.bg_editorial_consultation),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.14f,
                modifier = Modifier.fillMaxSize()
            )

            // Main Consultation Viewport
            if (uiState.isAudioOnlyFallback || !uiState.isCameraOn) {
                // Audio-First Calm Presence
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    TheSphere(size = 90.dp, isBreathing = true)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = appointment?.practitionerName ?: "Dr. Mwansa Chileshe",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = CalmWhite
                        )
                    )
                    Text(
                        text = appointment?.practitionerTitle ?: "Clinical Psychologist",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSoftSlate
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = Color(0xFF162A45),
                        border = BorderStroke(1.dp, Color(0xFF264366))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CalmEmerald)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Audio-First Encrypted Channel",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Switched to audio-only to preserve call quality on your current connection.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSoftSlate,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                // Video Viewport with Soft Rounded Corners (Radius 28dp)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 80.dp)
                        .clip(CalmLightShapes.Prominent)
                        .background(Color(0xFF162840)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TheSphere(size = 72.dp, isBreathing = true)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "${appointment?.practitionerName ?: "Dr. Mwansa Chileshe"}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                color = CalmWhite
                            )
                        )
                    }

                    // Self-preview PIP
                    Surface(
                        shape = CalmLightShapes.Standard,
                        color = Color(0xFF243B5C),
                        border = BorderStroke(1.dp, Color(0xFF385882)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(width = 90.dp, height = 120.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "You",
                                color = CalmWhite,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily
                                )
                            )
                        }
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
                // Duration pill
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
                            text = formattedDuration,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmWhite
                            )
                        )
                    }
                }

                // Connection Quality Pill
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = Color(0xFF13253D).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color(0xFF243C5E))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SignalCellularAlt,
                            contentDescription = null,
                            tint = CalmEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.connectionQuality.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmWhite
                            )
                        )
                    }
                }
            }

            // Floating Bottom Control Pill
            Surface(
                shape = CalmLightShapes.Pill,
                color = Color(0xFF13253D).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color(0xFF2A466D)),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Mic toggle
                    IconButton(
                        onClick = { viewModel.toggleMic() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (uiState.isMicOn) Color(0xFF233D61) else CalmCrisisCoral.copy(alpha = 0.8f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (uiState.isMicOn) Icons.Outlined.Mic else Icons.Outlined.MicOff,
                            contentDescription = "Mute",
                            tint = CalmWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Video toggle
                    IconButton(
                        onClick = { viewModel.toggleCamera() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (uiState.isCameraOn) Color(0xFF233D61) else CalmCrisisCoral.copy(alpha = 0.8f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (uiState.isCameraOn) Icons.Outlined.Videocam else Icons.Outlined.VideocamOff,
                            contentDescription = "Camera",
                            tint = CalmWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Audio-only toggle
                    IconButton(
                        onClick = { viewModel.toggleAudioOnlyFallback() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (uiState.isAudioOnlyFallback) CalmSphereBlue else Color(0xFF233D61),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NetworkCheck,
                            contentDescription = "Bandwidth fallback",
                            tint = CalmWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // In-call Chat
                    IconButton(
                        onClick = { viewModel.toggleChat() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (uiState.isChatOpen) CalmSphereBlue else Color(0xFF233D61), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = CalmWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // End call
                    IconButton(
                        onClick = { viewModel.promptLeaveCall() },
                        modifier = Modifier
                            .size(50.dp)
                            .background(CalmCrisisCoral, CircleShape)
                            .testTag("btn_leave_consultation")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = CalmWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            
            // Integrated Chat Overlay
            AnimatedVisibility(
                visible = uiState.isChatOpen,
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
                                onClick = { viewModel.toggleChat() },
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
                            // Mock message from doctor
                            Row(verticalAlignment = Alignment.Bottom) {
                                Surface(
                                    shape = CircleShape,
                                    color = CalmSphereBlue,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("Dr", color = CalmWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                                    color = Color(0xFF243C5E)
                                ) {
                                    Text(
                                        text = "Hello Kondwani, I'm reviewing your sleep journal now. Could you confirm if you're still waking up around 3 AM?",
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
