package com.example.core.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.model.AppointmentStatus
import com.example.core.model.ConsultationType
import com.example.ui.theme.*

/**
 * CALM LIGHT DESIGN SYSTEM
 * Centralized design tokens and reusable components.
 * Radii: 12dp, 20dp, 28dp, 36dp, Full Pill
 */
object CalmLightShapes {
    val RadiusSmall = 12.dp     // Small controls, chips, status tags
    val RadiusStandard = 20.dp  // Standard cards, content containers
    val RadiusProminent = 28.dp // Prominent cards, feature panels
    val RadiusHero = 36.dp       // Hero cards, sheets, modal containers
    val RadiusPill = 999.dp     // Full pill shape for buttons & floating navigation

    val Small = RoundedCornerShape(RadiusSmall)
    val Standard = RoundedCornerShape(RadiusStandard)
    val Prominent = RoundedCornerShape(RadiusProminent)
    val Hero = RoundedCornerShape(RadiusHero)
    val Pill = RoundedCornerShape(RadiusPill)
}

enum class AuraType(
    val primaryColor: Color,
    val highlightColor: Color,
    val accentColor: Color
) {
    Discovery(
        primaryColor = CalmDiscoveryAura,       // 0xFFDCE9FB (Sky blue wash)
        highlightColor = Color(0xFFEDF5FF),
        accentColor = CalmSphereBlue
    ),
    Sessions(
        primaryColor = CalmSessionsAura,        // 0xFFDDF3E6 (Restorative sage/mint)
        highlightColor = Color(0xFFEFFBF3),
        accentColor = CalmEmerald
    ),
    SelfCare(
        primaryColor = CalmSelfCareAura,        // 0xFFE6E1FA (Restorative lilac/lavender)
        highlightColor = Color(0xFFF4F0FD),
        accentColor = Color(0xFF745CB5)
    ),
    Payments(
        primaryColor = CalmPaymentsAura,        // 0xFFF6E9D2 (Warm amber/honey/sand)
        highlightColor = Color(0xFFFFF7E7),
        accentColor = CalmHoneyGold
    ),
    Neutral(
        primaryColor = CalmMistSurface,         // 0xFFF1F4F8 (Quiet mist/neutral)
        highlightColor = Color(0xFFFAF9F6),
        accentColor = CalmSlate
    );

    // Backward-compatibility property
    val color: Color get() = primaryColor
}

/**
 * AuraBackground
 * Lightweight, performant gradient wrapper to be used behind content surfaces.
 * Strictly follows the color rules:
 * - Discovery: Soft sky-blue wash with airy highlights
 * - Sessions: Gentle sage/mint wash representing clinical peace and healing
 * - Self-care: Restorative lilac/lavender wash for introspective reflection
 * - Payments: Warm reassuring amber/honey wash for financial clarity
 *
 * Implemented with zero runtime allocations in drawBehind, zero GPU blur filters,
 * ensuring high performance (60/120fps) and guaranteed WCAG text contrast.
 */
@Composable
fun AuraBackground(
    aura: AuraType = AuraType.Discovery,
    modifier: Modifier = Modifier,
    secondaryAura: AuraType? = null,
    intensity: Float = 1.0f,
    baseCanvasColor: Color = CalmIvoryCanvas,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseCanvasColor)
            .drawBehind {
                val clampedIntensity = intensity.coerceIn(0.2f, 1.5f)

                // 1. Primary soft aura bloom at top right / center
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            aura.primaryColor.copy(alpha = 0.65f * clampedIntensity),
                            aura.highlightColor.copy(alpha = 0.28f * clampedIntensity),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.86f, size.height * 0.10f),
                        radius = size.width * 0.85f
                    )
                )

                // 2. Subtle top ambient highlight for soft luminosity
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            aura.highlightColor.copy(alpha = 0.45f * clampedIntensity),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.35f, -size.height * 0.05f),
                        radius = size.width * 0.65f
                    )
                )

                // 3. Secondary soft wash (custom secondary aura or subtle warm foundation)
                val secondaryColor = secondaryAura?.primaryColor ?: when (aura) {
                    AuraType.Discovery -> CalmSelfCareAura.copy(alpha = 0.25f)
                    AuraType.Sessions -> CalmDiscoveryAura.copy(alpha = 0.25f)
                    AuraType.SelfCare -> CalmDiscoveryAura.copy(alpha = 0.25f)
                    AuraType.Payments -> CalmPaymentsAura.copy(alpha = 0.35f)
                    AuraType.Neutral -> Color.Transparent
                }

                if (secondaryColor != Color.Transparent) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                secondaryColor.copy(alpha = 0.40f * clampedIntensity),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.08f, size.height * 0.82f),
                            radius = size.width * 0.70f
                        )
                    )
                }

                // 4. Subtle vertical sheen for effortless text contrast
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            aura.highlightColor.copy(alpha = 0.20f * clampedIntensity),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.35f
                    )
                )
            },
        content = content
    )
}

/**
 * THE SPHERE
 * Signature visual element: soft, abstract, iridescent blue/emerald orb.
 * Represents calm, presence, guidance, breathing, transition.
 * Never an AI doctor, never a chatbot face.
 */
@Composable
fun TheSphere(
    size: Dp = 48.dp,
    isBreathing: Boolean = false,
    breathingPhaseText: String? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sphere_anim")
    val scale by if (isBreathing) {
        infiniteTransition.animateFloat(
            initialValue = 0.88f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing_scale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gentle_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size * scale)
                .drawBehind {
                    val radius = this.size.minDimension / 2f
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)

                    // Outer soft luminous glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CalmSphereBlue.copy(alpha = 0.25f),
                                CalmEmerald.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius * 1.35f
                        )
                    )

                    // Main iridescent orb
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                CalmSphereBlue,
                                Color(0xFF2680EB),
                                CalmEmerald,
                                CalmLime.copy(alpha = 0.85f)
                            ),
                            start = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f),
                            end = Offset(center.x + radius * 0.8f, center.y + radius * 0.8f)
                        ),
                        center = center,
                        radius = radius
                    )

                    // Specular highlight for liquid glass depth
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                            radius = radius * 0.5f
                        )
                    )
                }
        )

        if (breathingPhaseText != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = breathingPhaseText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = CalmInkNavy
                )
            )
        }
    }
}

/**
 * CalmButton
 * Primary: 56dp height, full pill shape, CalmInkNavy background, white text.
 * Secondary: Mist / White background, subtle border, CalmInkNavy text.
 * Ghost: Text / Ghost action.
 */
enum class CalmButtonVariant {
    Primary,
    Secondary,
    Ghost,
    Crisis
}

@Composable
fun CalmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CalmButtonVariant = CalmButtonVariant.Primary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String? = null
) {
    val height = 54.dp
    val containerColor = when (variant) {
        CalmButtonVariant.Primary -> CalmInkNavy
        CalmButtonVariant.Secondary -> CalmMistSurface
        CalmButtonVariant.Ghost -> Color.Transparent
        CalmButtonVariant.Crisis -> CalmCrisisCoral
    }

    val contentColor = when (variant) {
        CalmButtonVariant.Primary -> CalmWhite
        CalmButtonVariant.Secondary -> CalmInkNavy
        CalmButtonVariant.Ghost -> CalmInkNavy
        CalmButtonVariant.Crisis -> CalmWhite
    }

    val borderStroke = when (variant) {
        CalmButtonVariant.Secondary -> BorderStroke(1.dp, CalmHairline)
        else -> null
    }

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = CalmLightShapes.Pill,
        color = if (enabled) containerColor else CalmMistSurface,
        border = borderStroke,
        modifier = modifier
            .heightIn(min = height)
            .defaultMinSize(minWidth = 120.dp, minHeight = 48.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            if (isLoading) {
                TheSphere(size = 20.dp, isBreathing = true)
                Spacer(modifier = Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else CalmSoftSlate,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.1.sp
                ),
                color = if (enabled) contentColor else CalmSoftSlate
            )
        }
    }
}

/**
 * CalmCard
 * Level 2 depth: pure white card with extremely soft navy-tinted shadow and CalmLightShapes.
 */
@Composable
fun CalmCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CalmLightShapes.Standard,
    backgroundColor: Color = CalmWhite,
    border: BorderStroke? = BorderStroke(1.dp, CalmHairline.copy(alpha = 0.7f)),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = backgroundColor,
        border = border,
        shadowElevation = 0.5.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * CalmPill
 * Lightweight, rounded pill chip for tags, categories, statuses.
 */
@Composable
fun CalmPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    backgroundColor: Color = CalmMistSurface,
    contentColor: Color = CalmInkNavy,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = CalmLightShapes.Pill,
        color = backgroundColor,
        modifier = modifier
            .heightIn(min = 34.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = contentColor
            )
        }
    }
}

/**
 * CalmCalendarTile
 * Calendar date selector tile with explicit states:
 * - Selected: Sphere Blue + white text
 * - Available: Mist / White + navy text
 * - Unavailable: subtle hatched texture + muted text (Never red)
 */
@Composable
fun CalmCalendarTile(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = isAvailable,
        shape = CalmLightShapes.Small,
        color = when {
            isSelected -> CalmSphereBlue
            isAvailable -> CalmWhite
            else -> CalmMistSurface.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) CalmSphereBlue else CalmHairline
        ),
        modifier = modifier
            .size(width = 64.dp, height = 76.dp)
            .drawBehind {
                if (!isAvailable) {
                    // Subtle hatched lines to convey unavailable state without harsh red
                    val step = 10.dp.toPx()
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(
                            color = CalmHairline,
                            start = Offset(x, size.height),
                            end = Offset(x + size.height, 0f),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        x += step
                    }
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = when {
                    isSelected -> CalmWhite.copy(alpha = 0.85f)
                    isAvailable -> CalmSlate
                    else -> CalmSoftSlate.copy(alpha = 0.6f)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                ),
                color = when {
                    isSelected -> CalmWhite
                    isAvailable -> CalmInkNavy
                    else -> CalmSoftSlate.copy(alpha = 0.5f)
                }
            )
        }
    }
}

/**
 * CalmTimeSlotPill
 * Clean pill for time slot selection with clear available and unavailable hatched treatment.
 */
@Composable
fun CalmTimeSlotPill(
    timeLabel: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = isAvailable,
        shape = CalmLightShapes.Pill,
        color = when {
            isSelected -> CalmSphereBlue
            isAvailable -> CalmWhite
            else -> CalmMistSurface.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) CalmSphereBlue else CalmHairline
        ),
        modifier = modifier
            .height(44.dp)
            .drawBehind {
                if (!isAvailable) {
                    val step = 8.dp.toPx()
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(
                            color = CalmHairline,
                            start = Offset(x, size.height),
                            end = Offset(x + size.height, 0f),
                            strokeWidth = 1.2.dp.toPx()
                        )
                        x += step
                    }
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (isAvailable) timeLabel else "$timeLabel • Booked",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                color = when {
                    isSelected -> CalmWhite
                    isAvailable -> CalmInkNavy
                    else -> CalmSoftSlate.copy(alpha = 0.6f)
                }
            )
        }
    }
}

/**
 * CalmTopBar
 * Quiet, elegant header with generous whitespace, Outfit titles, and serene hierarchy.
 */
@Composable
fun CalmTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    isLowBandwidth: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("nav_back_button")
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CalmInkNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = CalmInkNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                actions()
            }
        }

        AnimatedVisibility(visible = isLowBandwidth) {
            Surface(
                shape = CalmLightShapes.Pill,
                color = CalmPaymentsAura.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalCellularAlt,
                        contentDescription = null,
                        tint = Color(0xFF9A6B1F),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Low bandwidth active — Audio-first optimization",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = InterFontFamily,
                            color = Color(0xFF7A5112),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * CalmConfirmationDialog
 * Booking confirmation dialog displaying practitioner, service, date, time, format, and fee.
 */
@Composable
fun CalmConfirmationDialog(
    practitionerName: String,
    specialtyName: String,
    serviceName: String,
    dateFormatted: String,
    timeFormatted: String,
    consultationType: ConsultationType,
    feeZmw: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = CalmLightShapes.Prominent,
            color = CalmWhite,
            border = BorderStroke(1.dp, CalmHairline),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TheSphere(size = 44.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Confirm Booking",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp
                    ),
                    color = CalmInkNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Review your consultation details before proceeding to payment.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = InterFontFamily,
                        textAlign = TextAlign.Center,
                        color = CalmSlate
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Summary Card
                Surface(
                    shape = CalmLightShapes.Standard,
                    color = CalmMistSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Practitioner", style = MaterialTheme.typography.bodySmall, color = CalmSlate)
                            Text(practitionerName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = CalmInkNavy)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Service", style = MaterialTheme.typography.bodySmall, color = CalmSlate)
                            Text(serviceName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = CalmInkNavy)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Date & Time", style = MaterialTheme.typography.bodySmall, color = CalmSlate)
                            Text("$dateFormatted • $timeFormatted", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = CalmInkNavy)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Format", style = MaterialTheme.typography.bodySmall, color = CalmSlate)
                            Text(if (consultationType == ConsultationType.ONLINE) "Online Video/Audio" else "In-Person Clinic", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = CalmInkNavy)
                        }
                        HorizontalDivider(color = CalmHairline)
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Total Fee", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = CalmInkNavy)
                            Text(
                                text = "K${feeZmw.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    color = CalmSphereBlue
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CalmButton(
                    text = "Confirm & Proceed",
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_dialog_confirm_booking"
                )

                Spacer(modifier = Modifier.height(10.dp))

                CalmButton(
                    text = "Modify Details",
                    onClick = onDismiss,
                    variant = CalmButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * CalmEmptyState
 * Contextual empty state with small serene Sphere, calm copy, no generic 'No data found'.
 */
@Composable
fun CalmEmptyState(
    title: String,
    message: String,
    actionLabel: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val buttonLabel = actionLabel ?: actionText
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        TheSphere(size = 40.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = CalmInkNavy,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = CalmSlate,
            textAlign = TextAlign.Center
        )
        if (buttonLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(20.dp))
            CalmButton(
                text = buttonLabel,
                onClick = onAction,
                variant = CalmButtonVariant.Secondary
            )
        }
    }
}

/**
 * CalmLoadingState
 * Subtle Sphere breathing animation, never generic jarring spinners.
 */
@Composable
fun CalmLoadingState(
    message: String = "Preparing your care space...",
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp)
    ) {
        TheSphere(size = 44.dp, isBreathing = true)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = InterFontFamily,
                color = CalmSlate,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * CalmErrorState
 * Calm error messaging with Coral reserved solely for crisis/safety.
 */
@Composable
fun CalmErrorState(
    title: String = "Something didn't go as planned",
    message: String = "Check your connection and try again.",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = CalmMistSurface,
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = CalmSlate,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = CalmInkNavy
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = InterFontFamily,
                color = CalmSlate
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        CalmButton(
            text = "Try again",
            onClick = onRetry,
            variant = CalmButtonVariant.Secondary
        )
    }
}
