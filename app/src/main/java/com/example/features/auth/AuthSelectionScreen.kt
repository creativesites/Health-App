package com.example.features.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.UserRole
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthSelectionScreen(
    onLoginSuccess: (role: UserRole) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedRole by remember { mutableStateOf(UserRole.USER) }
    var selectedSpecialistId by remember { mutableStateOf("doc_chileshe_01") }
    var isSigningIn by remember { mutableStateOf(false) }

    val bgAura = if (selectedRole == UserRole.USER) AuraType.Discovery else AuraType.Sessions
    val secondaryAura = if (selectedRole == UserRole.USER) AuraType.Payments else AuraType.Payments

    AuraBackground(
        aura = bgAura,
        secondaryAura = secondaryAura,
        intensity = 1.25f
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Location & Registry Pill
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmWhite.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp
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
                            text = "Calm Light • Lusaka, Zambia",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Official Zambia Healthcare Logo
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(92.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_app_logo),
                            contentDescription = "Zambia Healthcare Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Zambia Health Sanctuary",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = CalmInkNavy,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "A dual-sided healthcare & mental wellness platform. Choose how you want to experience the application today:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate,
                        fontSize = 14.5.sp,
                        lineHeight = 21.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Gateway Card 1: Patient / Citizen Sanctuary
                GatewayRoleCard(
                    role = UserRole.USER,
                    isSelected = selectedRole == UserRole.USER,
                    badgeText = "PATIENT SANCTUARY",
                    badgeColor = CalmSphereBlue,
                    badgeBg = CalmDiscoveryAura,
                    icon = Icons.Outlined.Spa,
                    title = "Individual Seeking Care",
                    subtitle = "Browse verified specialists, book teleconsultations, log daily mindful rhythms, and pay seamlessly via Mobile Money.",
                    personaPreviewName = "Kondwani Tembo",
                    personaPreviewSubtitle = "Patient • Lusaka, Zambia",
                    features = listOf(
                        "HPCZ-verified therapists, psychiatrists & counsellors",
                        "Live HD video consultations & clinic appointments",
                        "Daily breathing sphere, mood grounding & self-care hub",
                        "MTN MoMo & Airtel Money Zambian instant checkout"
                    ),
                    testTag = "gateway_patient_card",
                    onClick = { selectedRole = UserRole.USER }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gateway Card 2: Healthcare Specialist Practice
                GatewayRoleCard(
                    role = UserRole.SPECIALIST,
                    isSelected = selectedRole == UserRole.SPECIALIST,
                    badgeText = "CLINICAL WORKSPACE",
                    badgeColor = CalmEmerald,
                    badgeBg = CalmSessionsAura,
                    icon = Icons.Outlined.MedicalServices,
                    title = "Healthcare Specialist",
                    subtitle = "Manage clinical schedule, conduct live patient consultations, record HPCZ encounter notes, and coordinate care plans.",
                    personaPreviewName = if (selectedSpecialistId == "doc_chileshe_01") "Dr. Mutale Chileshe" else "Dr. Mwansa Kapwepwe",
                    personaPreviewSubtitle = if (selectedSpecialistId == "doc_chileshe_01") "Clinical Psychologist • HPCZ #7821-ZM" else "Consultant Psychiatrist • HPCZ #5490-ZM",
                    features = listOf(
                        "Clinical schedule & instant booking approval workflow",
                        "Active patient care roster with intake summaries",
                        "Encrypted teleconsultation room with live duration timer",
                        "SOAP-style clinical encounter notes & care plans"
                    ),
                    testTag = "gateway_specialist_card",
                    onClick = { selectedRole = UserRole.SPECIALIST },
                    specialistSelector = {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "Select Practitioner Profile:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmSlate,
                                    fontSize = 11.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SpecialistChip(
                                    name = "Dr. Mutale C.",
                                    title = "Psychologist",
                                    isSelected = selectedSpecialistId == "doc_chileshe_01",
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedSpecialistId = "doc_chileshe_01" }
                                )
                                SpecialistChip(
                                    name = "Dr. Mwansa K.",
                                    title = "Psychiatrist",
                                    isSelected = selectedSpecialistId == "doc_kapwepwe_02",
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedSpecialistId = "doc_kapwepwe_02" }
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Primary Enter Button (Styled matching Patient Home Pill Action)
                Button(
                    onClick = {
                        isSigningIn = true
                        coroutineScope.launch {
                            if (selectedRole == UserRole.USER) {
                                AppRepositoryLocator.authRepository.loginAsUser()
                                onLoginSuccess(UserRole.USER)
                            } else {
                                AppRepositoryLocator.authRepository.loginAsSpecialist(selectedSpecialistId)
                                onLoginSuccess(UserRole.SPECIALIST)
                            }
                            isSigningIn = false
                        }
                    },
                    shape = CalmLightShapes.Pill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == UserRole.USER) CalmDarkMatte else CalmEmerald,
                        contentColor = CalmWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("auth_enter_portal_btn"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            color = CalmWhite,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedRole == UserRole.USER) "Enter Patient Sanctuary" else "Open Clinical Practice",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = if (selectedRole == UserRole.USER) Icons.Default.ArrowForward else Icons.Default.MedicalServices,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Regulatory footer
                Surface(
                    shape = CalmLightShapes.Standard,
                    color = CalmWhite.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, CalmHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            tint = CalmEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "HPCZ Regulated • Strict Role Separation • Lusaka Healthcare Circle",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GatewayRoleCard(
    role: UserRole,
    isSelected: Boolean,
    badgeText: String,
    badgeColor: Color,
    badgeBg: Color,
    icon: ImageVector,
    title: String,
    subtitle: String,
    personaPreviewName: String,
    personaPreviewSubtitle: String,
    features: List<String>,
    testTag: String,
    onClick: () -> Unit,
    specialistSelector: @Composable (() -> Unit)? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) badgeColor else CalmHairline,
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Surface(
        shape = RoundedCornerShape(26.dp),
        color = CalmWhite,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Badge & Radio Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = badgeBg
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                fontSize = 10.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                // Selection check indicator
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) badgeColor else CalmMistSurface,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = CalmWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon + Title Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = badgeBg,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = CalmInkNavy
                    )
                    Text(
                        text = if (role == UserRole.USER) "Citizen & Patient Account" else "Clinician Practice Workspace",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSlate,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Demo Persona Preview chip
            Surface(
                shape = CalmLightShapes.Small,
                color = CalmMistSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CalmWhite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = personaPreviewName.take(1),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = personaPreviewName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = personaPreviewSubtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            specialistSelector?.invoke()

            Spacer(modifier = Modifier.height(12.dp))

            // Features check list
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = badgeColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmInkNavy.copy(alpha = 0.85f),
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialistChip(
    name: String,
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CalmLightShapes.Small,
        color = if (isSelected) CalmSessionsAura else CalmWhite,
        border = BorderStroke(1.dp, if (isSelected) CalmEmerald else CalmHairline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) CalmEmerald else CalmInkNavy,
                    fontSize = 12.sp
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSlate,
                    fontSize = 10.sp
                )
            )
        }
    }
}
