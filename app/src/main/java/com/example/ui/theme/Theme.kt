package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CalmLightColorScheme = lightColorScheme(
    primary = CalmInkNavy,
    onPrimary = CalmWhite,
    primaryContainer = CalmMistSurface,
    onPrimaryContainer = CalmInkNavy,
    secondary = CalmSphereBlue,
    onSecondary = CalmWhite,
    secondaryContainer = CalmDiscoveryAura,
    onSecondaryContainer = CalmInkNavy,
    tertiary = CalmEmerald,
    onTertiary = CalmWhite,
    tertiaryContainer = CalmSessionsAura,
    onTertiaryContainer = CalmInkNavy,
    background = CalmIvoryCanvas,
    onBackground = CalmInkNavy,
    surface = CalmWhite,
    onSurface = CalmInkNavy,
    surfaceVariant = CalmMistSurface,
    onSurfaceVariant = CalmSlate,
    outline = CalmSoftSlate,
    outlineVariant = CalmHairline
)

private val CalmDarkColorScheme = darkColorScheme(
    primary = HealthTealDarkPrimary,
    onPrimary = HealthTealDarkOnPrimary,
    primaryContainer = HealthTealDarkPrimaryContainer,
    onPrimaryContainer = HealthTealDarkOnPrimaryContainer,
    secondary = HealthDarkSecondary,
    onSecondary = HealthDarkOnSecondary,
    secondaryContainer = HealthDarkSecondaryContainer,
    onSecondaryContainer = HealthDarkOnSecondaryContainer,
    background = HealthDarkBackground,
    onBackground = HealthDarkOnBackground,
    surface = HealthDarkSurface,
    onSurface = HealthDarkOnSurface,
    surfaceVariant = HealthDarkSurfaceVariant,
    onSurfaceVariant = HealthDarkOnSurfaceVariant,
    outline = CalmSoftSlate,
    outlineVariant = Color(0xFF22344B)
)

@Composable
fun HealthcareTheme(
    darkTheme: Boolean = false, // Calm Light is our signature primary visual language
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CalmDarkColorScheme else CalmLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for any existing test scaffolding
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    HealthcareTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

