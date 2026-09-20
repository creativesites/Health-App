package com.example.features.messaging

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.core.design.*
import com.example.core.model.UserRole
import com.example.ui.theme.*

@Composable
fun MessagingScreen(
    viewModel: MessagingViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    AuraBackground(aura = AuraType.Discovery) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = uiState.participantName,
                    subtitle = "Confidential clinical channel",
                    onBack = onBack
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Privacy reassurance banner
                Surface(
                    color = CalmWhite.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, CalmHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = CalmSphereBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "End-to-end encrypted medical discussion with your care provider.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Messages list
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(uiState.messages) { message ->
                        val isViewerPatient = uiState.currentUserRole == UserRole.USER
                        val isMe = if (isViewerPatient) message.isFromPatient else !message.isFromPatient
                        val bubbleColor = if (isMe) (if (isViewerPatient) CalmSphereBlue else CalmEmerald) else CalmWhite

                        Row(
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (!isMe) {
                                ProviderAvatar(
                                    initials = if (message.isFromPatient) "PT" else "DR",
                                    size = 32,
                                    backgroundColor = if (message.isFromPatient) CalmDiscoveryAura else CalmSessionsAura,
                                    modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                ),
                                color = bubbleColor,
                                border = if (isMe) null else BorderStroke(1.dp, CalmHairline),
                                shadowElevation = if (isMe) 1.dp else 0.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = message.text,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            color = if (isMe) CalmWhite else CalmInkNavy,
                                            lineHeight = 20.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = message.timestampFormatted,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = InterFontFamily,
                                                fontSize = 10.sp,
                                                color = if (isMe) CalmWhite.copy(alpha = 0.7f) else CalmSoftSlate
                                            )
                                        )
                                        if (isMe) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.DoneAll,
                                                contentDescription = "Delivered",
                                                tint = CalmWhite.copy(alpha = 0.8f),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Bar
                Surface(
                    color = CalmWhite,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, CalmHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.currentInput,
                            onValueChange = { viewModel.onInputChange(it) },
                            placeholder = {
                                Text(
                                    "Type message...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSoftSlate
                                    )
                                )
                            },
                            maxLines = 4,
                            shape = CalmLightShapes.Pill,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CalmWhite,
                                unfocusedContainerColor = CalmWhite,
                                focusedBorderColor = CalmSphereBlue,
                                unfocusedBorderColor = CalmHairline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_message_text")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            enabled = uiState.currentInput.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("btn_send_message")
                                .background(
                                    color = if (uiState.currentInput.isNotBlank()) CalmSphereBlue else CalmMistSurface,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (uiState.currentInput.isNotBlank()) CalmWhite else CalmSoftSlate,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
