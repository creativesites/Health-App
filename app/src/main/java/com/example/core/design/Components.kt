package com.example.core.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.AppointmentStatus
import com.example.core.model.ConsultationType
import com.example.core.model.Patient
import com.example.core.model.PaymentStatus
import com.example.ui.theme.*
import coil.compose.AsyncImage

import com.example.R

@Composable
fun HealthcareTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    isLowBandwidth: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CalmTopBar(
        title = title,
        subtitle = subtitle,
        onBack = onNavigationClick,
        isLowBandwidth = isLowBandwidth,
        actions = actions
    )
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Surface(
        shape = CalmLightShapes.Pill,
        color = CalmSessionsAura.copy(alpha = 0.8f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_util_verified_specialist),
                contentDescription = "HPCZ Verified",
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "HPCZ Verified",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = CalmInkNavy
            )
        }
    }
}

@Composable
fun AppointmentStatusChip(status: AppointmentStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple(CalmSessionsAura, CalmEmerald, Icons.Outlined.CheckCircle)
        AppointmentStatus.PENDING_PAYMENT -> Triple(CalmPaymentsAura, Color(0xFF9A6B1F), Icons.Outlined.HourglassTop)
        AppointmentStatus.IN_PROGRESS -> Triple(CalmDiscoveryAura, CalmSphereBlue, Icons.Outlined.Videocam)
        AppointmentStatus.COMPLETED -> Triple(CalmMistSurface, CalmSlate, Icons.Outlined.DoneAll)
        AppointmentStatus.CANCELLED -> Triple(CalmMistSurface, CalmSoftSlate, Icons.Outlined.Close)
        AppointmentStatus.RESCHEDULED -> Triple(CalmSelfCareAura, Color(0xFF6B4FA8), Icons.Outlined.Schedule)
        else -> Triple(CalmMistSurface, CalmSlate, Icons.Outlined.Info)
    }

    Surface(
        shape = CalmLightShapes.Pill,
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.label,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun ConsultationTypeChip(type: ConsultationType, modifier: Modifier = Modifier) {
    val isOnline = type == ConsultationType.ONLINE
    Surface(
        shape = CalmLightShapes.Pill,
        color = if (isOnline) CalmDiscoveryAura.copy(alpha = 0.7f) else CalmMistSurface,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = if (isOnline) R.drawable.ic_util_video_consultation else R.drawable.ic_util_location),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isOnline) "Online" else "In-Person",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isOnline) CalmSphereBlue else CalmSlate
            )
        }
    }
}

@Composable
fun ProviderAvatar(
    initials: String,
    size: Int = 54,
    backgroundColor: Color = CalmMistSurface,
    imageResId: Int? = null,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, CalmHairline, CircleShape)
    ) {
        if (imageResId != null) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = CalmInkNavy,
                    fontSize = (size / 2.7).sp
                )
            )
        }
    }
}

@Composable
fun PatientAvatar(
    patient: Patient?,
    size: Int = 64,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = clickableModifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                when (patient?.avatarPresetId) {
                    "preset_amber" -> CalmHoneyGoldLight
                    "preset_blue" -> CalmSphereBlue.copy(alpha = 0.15f)
                    "preset_emerald" -> CalmEmerald.copy(alpha = 0.15f)
                    "preset_lavender" -> CalmDiscoveryAura
                    else -> CalmDiscoveryAura
                }
            )
            .border(
                width = 1.5.dp,
                color = when (patient?.avatarPresetId) {
                    "preset_amber" -> CalmHoneyGold
                    "preset_blue" -> CalmSphereBlue
                    "preset_emerald" -> CalmEmerald
                    else -> CalmSphereBlue.copy(alpha = 0.35f)
                },
                shape = CircleShape
            )
    ) {
        if (!patient?.avatarUri.isNullOrBlank()) {
            AsyncImage(
                model = patient?.avatarUri,
                contentDescription = patient?.fullName ?: "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val initials = patient?.initials ?: "ME"
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = when (patient?.avatarPresetId) {
                        "preset_amber" -> CalmHoneyGoldDark
                        "preset_blue" -> CalmSphereBlue
                        "preset_emerald" -> CalmEmerald
                        else -> CalmSphereBlue
                    },
                    fontSize = (size / 2.6).sp
                )
            )
        }
    }
}

@Composable
fun HealthcareCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    CalmCard(
        modifier = modifier,
        shape = CalmLightShapes.Standard,
        onClick = onClick,
        content = content
    )
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    CalmEmptyState(
        title = title,
        message = message,
        actionLabel = actionLabel,
        onAction = onAction,
        modifier = modifier
    )
}

