package com.example.features.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.Patient
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Immersive, non-sticky home header.
 *
 * Scrolls with the page (no Scaffold topBar). The hero is a lit, floating "stage":
 *  - press-and-hold tilts the card toward your finger and springs back (3D press)
 *  - the orb bobs on a cast ground shadow, and layers shift at different rates (parallax)
 *  - as you scroll, the card recedes (tilts back, scales down, fades) instead of leaving a bar behind
 *  - the next-session tile floats above the card edge on its own depth plane
 */
@Composable
fun HomeHeroHeader(
    patient: Patient?,
    firstName: String,
    unreadCount: Int,
    streakDays: Int,
    sessionCount: Int,
    nextSessionTitle: String,
    nextSessionSubtitle: String,
    scrollState: ScrollState,
    onNavigateToProfile: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onNextSessionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HomeTopRow(
            patient = patient,
            unreadCount = unreadCount,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToSafety = onNavigateToSafety,
            onNavigateToNotifications = onNavigateToNotifications,
            onSwitchRoleClick = onSwitchRoleClick
        )
        Spacer(modifier = Modifier.height(18.dp))
        HeroStage(
            firstName = firstName,
            streakDays = streakDays,
            sessionCount = sessionCount,
            nextSessionTitle = nextSessionTitle,
            nextSessionSubtitle = nextSessionSubtitle,
            scrollState = scrollState,
            onNextSessionClick = onNextSessionClick
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top row: avatar + greeting on the left, floating glass control dock on the right
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopRow(
    patient: Patient?,
    unreadCount: Int,
    onNavigateToProfile: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSwitchRoleClick: () -> Unit
) {
    val greeting = remember { greetingForNow() }
    val dateLabel = remember { SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp)
    ) {
        // Avatar with a soft light-catching ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(CalmHoneyGold, CalmWhite, CalmSphereBlue, CalmHoneyGold)
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            PatientAvatar(patient = patient, size = 44, onClick = onNavigateToProfile)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                color = CalmSlate,
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                maxLines = 1
            )
            Text(
                text = dateLabel,
                color = CalmInkNavy,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Floating glass dock
        Surface(
            shape = CircleShape,
            color = CalmWhite.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, Brush.verticalGradient(listOf(CalmWhite, CalmHairline))),
            shadowElevation = 10.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                DockButton(
                    background = CalmCrisisCoralSoft,
                    tag = "home_safety_button",
                    onClick = onNavigateToSafety
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = "Crisis support",
                        tint = CalmCrisisCoral,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DockButton(
                    background = Color.Transparent,
                    tag = "home_notifications_button",
                    onClick = onNavigateToNotifications
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = CalmSphereBlue) {
                                    Text("$unreadCount", color = CalmWhite, fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = CalmInkNavy,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                DockButton(
                    background = CalmDiscoveryAura,
                    tag = "home_switch_role_button",
                    onClick = onSwitchRoleClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = "Switch role",
                        tint = CalmSphereBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DockButton(
    background: Color,
    tag: String,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick)
            .testTag(tag),
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero stage
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroStage(
    firstName: String,
    streakDays: Int,
    sessionCount: Int,
    nextSessionTitle: String,
    nextSessionSubtitle: String,
    scrollState: ScrollState,
    onNextSessionClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotX = remember { Animatable(0f) } // degrees, driven by press
    val rotY = remember { Animatable(0f) }

    val idle = rememberInfiniteTransition(label = "hero_idle")
    val float = idle.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_float"
    )

    val cardShape = RoundedCornerShape(36.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(338.dp)
    ) {
        // ── Card (depth plane 0) ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(300.dp)
                .graphicsLayer {
                    // Recede as it scrolls away: tilt back, shrink, fade, lag slightly behind the scroll
                    val progress = (scrollState.value / size.height).coerceIn(0f, 1f)
                    // If the tilt feels inverted on your device, flip the signs on rotationX / rotationY.
                    rotationX = rotX.value + progress * 14f
                    rotationY = rotY.value
                    cameraDistance = 14f * density
                    val s = 1f - progress * 0.06f
                    scaleX = s
                    scaleY = s
                    alpha = 1f - progress * 0.35f
                    translationY = scrollState.value * 0.14f
                }
                .shadow(
                    elevation = 28.dp,
                    shape = cardShape,
                    ambientColor = CalmSphereBlue.copy(alpha = 0.35f),
                    spotColor = CalmSphereBlue.copy(alpha = 0.60f)
                )
                .drawBehind { drawHeroBackdrop() }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.04f))
                    ),
                    shape = cardShape
                )
                .pointerInput(Unit) {
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    detectTapGestures(
                        onPress = { touch ->
                            val nx = (touch.x / w - 0.5f) * 2f
                            val ny = (touch.y / h - 0.5f) * 2f
                            scope.launch {
                                rotY.animateTo(nx * 9f, spring(stiffness = Spring.StiffnessMedium))
                            }
                            scope.launch {
                                rotX.animateTo(-ny * 7f, spring(stiffness = Spring.StiffnessMedium))
                            }
                            tryAwaitRelease() // returns when released OR when a scroll takes over
                            scope.launch {
                                rotY.animateTo(
                                    0f,
                                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )
                            }
                            scope.launch {
                                rotX.animateTo(
                                    0f,
                                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )
                            }
                        }
                    )
                }
        ) {
            // Orb (depth plane 1: shifts opposite to the tilt + scroll parallax)
            HeroOrb(
                float = float,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = 18.dp)
                    .size(176.dp)
                    .graphicsLayer {
                        translationX = -rotY.value * 2.2f * density
                        translationY = scrollState.value * 0.10f + rotX.value * 2f * density
                    }
            )

            // Copy
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(0.58f)
                    .padding(start = 24.dp, top = 30.dp)
            ) {
                Text(
                    text = "Welcome,",
                    color = Color.White.copy(alpha = 0.72f),
                    fontFamily = InterFontFamily,
                    fontSize = 15.sp
                )
                Text(
                    text = firstName,
                    color = Color.White,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 34.sp,
                    letterSpacing = (-0.8).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your mental health sanctuary and clinical care circle.",
                    color = Color.White.copy(alpha = 0.74f),
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Glass stat chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 52.dp)
            ) {
                GlassChip(icon = Icons.Outlined.Spa, label = "$streakDays days")
                GlassChip(icon = Icons.Outlined.Psychology, label = "$sessionCount sessions")
            }
        }

        // ── Next-session tile (depth plane 2: floats over the card's lower edge) ──
        Surface(
            onClick = onNextSessionClick,
            shape = RoundedCornerShape(26.dp),
            color = CalmWhite,
            border = BorderStroke(1.dp, CalmHairline),
            shadowElevation = 16.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(76.dp)
                .graphicsLayer {
                    translationX = rotY.value * -2.6f * density
                    translationY = -rotX.value * 2.2f * density
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CalmSphereBlue, lerp(CalmSphereBlue, CalmInkNavy, 0.7f))
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Next session",
                        color = CalmSlate,
                        fontFamily = InterFontFamily,
                        fontSize = 11.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = nextSessionTitle,
                        color = CalmInkNavy,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = nextSessionSubtitle,
                        color = CalmSlate,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(CalmDarkMatte)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Open appointments",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CalmHoneyGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Drawing: card backdrop + the orb (the one memorable element)
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawHeroBackdrop() {
    // Base: deep celestial gradient
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                CalmSphereBlue,
                lerp(CalmSphereBlue, CalmInkNavy, 0.6f),
                CalmInkNavy
            ),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
    )

    // Warm light pooling behind the orb
    val glowCenter = Offset(size.width * 0.80f, size.height * 0.28f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(CalmHoneyGold.copy(alpha = 0.38f), Color.Transparent),
            center = glowCenter,
            radius = size.width * 0.55f
        ),
        radius = size.width * 0.55f,
        center = glowCenter
    )

    // Faint concentric arcs sweeping in from the bottom-left corner give the plane depth
    val origin = Offset(size.width * 0.05f, size.height * 1.02f)
    listOf(0.35f, 0.55f, 0.78f).forEachIndexed { i, f ->
        drawCircle(
            color = Color.White.copy(alpha = 0.06f - i * 0.015f),
            radius = size.width * f,
            center = origin,
            style = Stroke(width = 1.dp.toPx())
        )
    }

    // Top sheen: light falling from above
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
            endY = size.height * 0.45f
        )
    )
}

@Composable
private fun HeroOrb(float: State<Float>, modifier: Modifier = Modifier) {
    val gold = CalmHoneyGold
    val light = remember(gold) { lerp(gold, Color.White, 0.65f) }
    val deep = remember(gold) { lerp(gold, Color(0xFF7A3E00), 0.55f) }

    Canvas(modifier = modifier) {
        val bob = float.value // -1 (high) .. 1 (low)
        val r = size.minDimension * 0.31f
        val baseY = size.height * 0.46f
        val c = Offset(size.width / 2f, baseY + bob * 6.dp.toPx())

        // Cast shadow on the "floor": shrinks as the orb rises
        val shadowCenter = Offset(size.width / 2f, baseY + r + 30.dp.toPx())
        val shadowRadius = r * 1.5f * (1f + 0.08f * bob)
        scale(scaleX = 1f, scaleY = 0.22f, pivot = shadowCenter) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.38f), Color.Transparent),
                    center = shadowCenter,
                    radius = shadowRadius
                ),
                radius = shadowRadius,
                center = shadowCenter
            )
        }

        // Ring, back half (behind the orb)
        val ringW = size.width * 0.96f
        val ringH = ringW * 0.26f
        val ringTopLeft = Offset(c.x - ringW / 2f, c.y - ringH / 2f)
        val ringSize = Size(ringW, ringH)
        val ringStroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        rotate(degrees = -16f, pivot = c) {
            drawArc(
                color = Color.White.copy(alpha = 0.28f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = ringTopLeft,
                size = ringSize,
                style = ringStroke
            )
        }

        // Ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(gold.copy(alpha = 0.50f), Color.Transparent),
                center = c,
                radius = r * 2.0f
            ),
            radius = r * 2.0f,
            center = c
        )

        // Body: lit from the upper left
        drawCircle(
            brush = Brush.radialGradient(
                0f to light,
                0.45f to gold,
                1f to deep,
                center = Offset(c.x - r * 0.35f, c.y - r * 0.40f),
                radius = r * 1.55f
            ),
            radius = r,
            center = c
        )

        // Terminator: darkens the lower-right edge so it reads as a sphere
        drawCircle(
            brush = Brush.radialGradient(
                0.55f to Color.Transparent,
                1f to Color.Black.copy(alpha = 0.30f),
                center = Offset(c.x - r * 0.20f, c.y - r * 0.25f),
                radius = r * 1.35f
            ),
            radius = r,
            center = c
        )

        // Specular highlight
        val hl = Offset(c.x - r * 0.42f, c.y - r * 0.48f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.9f), Color.Transparent),
                center = hl,
                radius = r * 0.38f
            ),
            radius = r * 0.38f,
            center = hl
        )

        // Ring, front half (in front of the orb)
        rotate(degrees = -16f, pivot = c) {
            drawArc(
                color = Color.White.copy(alpha = 0.90f),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = ringTopLeft,
                size = ringSize,
                style = ringStroke
            )
        }
    }
}

private fun greetingForNow(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}