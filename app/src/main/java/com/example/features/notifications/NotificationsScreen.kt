package com.example.features.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.NotificationCategory
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val notificationRepository = remember { AppRepositoryLocator.notificationRepository }
    val notifications by notificationRepository.getNotifications().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    AuraBackground(aura = AuraType.Discovery) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = "Notifications",
                    subtitle = "Updates & session reminders",
                    onBack = onBack,
                    actions = {
                        TextButton(onClick = { coroutineScope.launch { notificationRepository.markAllAsRead() } }) {
                            Text(
                                "Mark all read",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSphereBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Privacy note
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
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = CalmSlate,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clinical details are never displayed on lockscreen previews.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                if (notifications.isEmpty()) {
                    CalmEmptyState(
                        title = "No notifications",
                        message = "You're all caught up with your updates and session reminders.",
                        modifier = Modifier.fillMaxSize().padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications) { item ->
                            val (icon, tint) = when (item.category) {
                                NotificationCategory.APPOINTMENT -> Pair(Icons.Outlined.CalendarToday, CalmSphereBlue)
                                NotificationCategory.PAYMENT -> Pair(Icons.Outlined.CheckCircle, CalmEmerald)
                                NotificationCategory.MESSAGE -> Pair(Icons.Outlined.ChatBubbleOutline, CalmSphereBlue)
                                NotificationCategory.CARE -> Pair(Icons.Outlined.Spa, CalmEmerald)
                                NotificationCategory.SYSTEM -> Pair(Icons.Outlined.Info, CalmSlate)
                            }

                            CalmCard(
                                shape = CalmLightShapes.Standard,
                                backgroundColor = if (!item.isRead) CalmWhite else CalmWhite.copy(alpha = 0.7f),
                                onClick = { coroutineScope.launch { notificationRepository.markAsRead(item.id) } }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = tint.copy(alpha = 0.12f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = if (!item.isRead) FontWeight.SemiBold else FontWeight.Normal
                                                ),
                                                color = CalmInkNavy
                                            )
                                            Text(
                                                text = item.timeAgo,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = InterFontFamily,
                                                    color = CalmSoftSlate,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.safePreview,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate
                                            )
                                        )
                                    }

                                    if (!item.isRead) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .size(8.dp)
                                                .background(CalmSphereBlue, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
