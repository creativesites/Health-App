package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Calm Light Core Palette
// Base
val CalmIvoryCanvas = Color(0xFFFBFAF7)
val CalmWhite = Color(0xFFFFFFFF)
val CalmMistSurface = Color(0xFFF1F4F8)

// Typography
val CalmInkNavy = Color(0xFF0B2545)
val CalmSlate = Color(0xFF4A5A70)
val CalmSoftSlate = Color(0xFF66758A)

// Accent
val CalmSphereBlue = Color(0xFF1F5FBF)
val CalmEmerald = Color(0xFF2EA84F)
val CalmLime = Color(0xFF8CD62A)
val CalmHairline = Color(0xFFE7ECF2)

// Warm Honey & Editorial Dark Contrast (from attached design philosophy)
val CalmHoneyGold = Color(0xFFFCD355)
val CalmHoneyGoldLight = Color(0xFFFFF6D6)
val CalmHoneyGoldSoft = Color(0xFFFFF6D6)
val CalmHoneyGoldDark = Color(0xFF7A5806)
val CalmDarkMatte = Color(0xFF181A1E)
val CalmDarkMatteSurface = Color(0xFF22252B)
val CalmDarkMatteBorder = Color(0xFF32363E)

// Crisis Only (Reserved exclusively for genuine crisis/safety states)
val CalmCrisisCoral = Color(0xFFC8402F)
val CalmCrisisCoralSoft = Color(0xFFFDF0EE)

// Aura System Washes
val CalmDiscoveryAura = Color(0xFFDCE9FB)
val CalmSessionsAura = Color(0xFFDDF3E6)
val CalmSelfCareAura = Color(0xFFE6E1FA)
val CalmPaymentsAura = Color(0xFFF6E9D2)

// Retain legacy aliases for backward compatibility with any domain components
val HealthTealPrimary = CalmInkNavy
val HealthTealOnPrimary = CalmWhite
val HealthTealPrimaryContainer = CalmMistSurface
val HealthTealOnPrimaryContainer = CalmInkNavy

val HealthSecondary = CalmSphereBlue
val HealthOnSecondary = CalmWhite
val HealthSecondaryContainer = CalmDiscoveryAura
val HealthOnSecondaryContainer = CalmInkNavy

val HealthTertiary = CalmEmerald
val HealthOnTertiary = CalmWhite
val HealthTertiaryContainer = CalmSessionsAura
val HealthOnTertiaryContainer = CalmInkNavy

val HealthBackgroundLight = CalmIvoryCanvas
val HealthOnBackgroundLight = CalmInkNavy
val HealthSurfaceLight = CalmWhite
val HealthOnSurfaceLight = CalmInkNavy
val HealthSurfaceVariantLight = CalmMistSurface
val HealthOnSurfaceVariantLight = CalmSlate
val HealthOutlineLight = CalmSoftSlate
val HealthOutlineVariantLight = CalmHairline

// Dark Theme Palette (Calm dark alternative)
val HealthDarkBackground = Color(0xFF0B1420)
val HealthDarkOnBackground = Color(0xFFF1F4F8)
val HealthDarkSurface = Color(0xFF131F30)
val HealthDarkOnSurface = Color(0xFFF1F4F8)
val HealthDarkSurfaceVariant = Color(0xFF1E2D42)
val HealthDarkOnSurfaceVariant = Color(0xFFA0B0C4)
val HealthTealDarkPrimary = Color(0xFF7AAAF0)
val HealthTealDarkOnPrimary = Color(0xFF0B2545)
val HealthTealDarkPrimaryContainer = Color(0xFF173860)
val HealthTealDarkOnPrimaryContainer = Color(0xFFDCE9FB)
val HealthDarkSecondary = Color(0xFF60C484)
val HealthDarkOnSecondary = Color(0xFF082E16)
val HealthDarkSecondaryContainer = Color(0xFF184E2E)
val HealthDarkOnSecondaryContainer = Color(0xFFDDF3E6)

// Functional Status Colors (Soft and calm)
val StatusConfirmed = CalmEmerald
val StatusPending = Color(0xFF9A6B1F) // Calm soft amber
val StatusCancelled = CalmSlate
val StatusInfo = CalmSphereBlue
val UrgentEmergencyRed = CalmCrisisCoral
val UrgentEmergencyLight = CalmCrisisCoralSoft

