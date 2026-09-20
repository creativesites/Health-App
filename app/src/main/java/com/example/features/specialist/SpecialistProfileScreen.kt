package com.example.features.specialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.core.design.*
import com.example.core.model.Practitioner
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SpecialistProfileScreen(
    onNavigateToAvailability: () -> Unit,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepo = AppRepositoryLocator.authRepository
    val specialistRepo = AppRepositoryLocator.specialistRepository

    val session by authRepo.getActiveSession().collectAsState(initial = null)
    val practitionerId = session?.practitionerId ?: "doc_chileshe_01"

    var practitioner by remember { mutableStateOf<Practitioner?>(null) }
    var isEditingBio by remember { mutableStateOf(false) }
    var bioText by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(practitionerId) {
        specialistRepo.getSpecialistProfile(practitionerId).collectLatest { p ->
            practitioner = p
            bioText = p?.bio ?: ""
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Switch Session or Sign Out?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        color = CalmInkNavy
                    )
                )
            },
            text = {
                Text(
                    text = "You can sign out of your specialist practice workspace and return to the role chooser or patient experience.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        coroutineScope.launch {
                            authRepo.logout()
                            onLogout()
                        }
                    }
                ) {
                    Text("Sign Out to Role Chooser", color = CalmCrisisCoral, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = CalmSlate)
                }
            }
        )
    }

    AuraBackground(
        aura = AuraType.Payments,
        secondaryAura = AuraType.Sessions,
        intensity = 1.15f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmWhite.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CalmEmerald)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "HPCZ Verified • Lusaka, Zambia",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    // Switch Account shortcut
                    Surface(
                        onClick = { showLogoutDialog = true },
                        shape = CircleShape,
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.SwapHoriz,
                                contentDescription = "Switch Account",
                                tint = CalmInkNavy,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Display
                Column {
                    Text(
                        text = "Clinical Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 30.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Credentials, practice settings & role account management",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 14.sp
                        )
                    )
                }

                // Verified Practitioner Header Card
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CalmSessionsAura,
                                border = BorderStroke(2.dp, CalmEmerald),
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = practitioner?.avatarInitials ?: "MC",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmEmerald,
                                            fontSize = 24.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = practitioner?.fullName ?: "Dr. Mutale Chileshe",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    VerifiedBadge()
                                }

                                Text(
                                    text = practitioner?.title ?: "Clinical Psychologist",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 14.sp
                                    )
                                )

                                Text(
                                    text = "HPCZ Registration: #7821-ZM",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmEmerald,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = CalmHairline)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Trio of Numerical Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Practice Base", style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate))
                                Text("Lusaka, ZM", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CalmInkNavy))
                            }
                            Column {
                                Text("Rating", style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate))
                                Text("★ 4.9 (48 Reviews)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CalmInkNavy))
                            }
                            Column {
                                Text("Languages", style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate))
                                Text("English, Bemba", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = CalmInkNavy))
                            }
                        }
                    }
                }

                // Account Switching Section (PROMINENT & BEAUTIFULLY STYLED)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = CalmDarkMatte,
                    border = BorderStroke(1.dp, CalmDarkMatteBorder),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF2A2E36),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.SwapHoriz,
                                            contentDescription = null,
                                            tint = CalmEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Account Role Switcher",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmWhite,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Text(
                                        text = "Switch cleanly between Patient and Specialist",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = Color(0xFFC7CDD8),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "To test the end-to-end platform or experience the Patient sanctuary as Kondwani Tembo, tap below to return to the portal gateway.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = Color(0xFFC7CDD8),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showLogoutDialog = true },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CalmWhite,
                                contentColor = CalmDarkMatte
                            ),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Role Chooser / Switch Account",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                // Weekly Availability Navigation Link
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToAvailability)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CalmSessionsAura,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.EditCalendar,
                                    contentDescription = null,
                                    tint = CalmEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weekly Availability Schedule",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "Configure consultation slots, virtual hours & clinic visits",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CalmSlate
                        )
                    }
                }

                // Professional Bio Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clinical Specialization & Bio",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy,
                                    fontSize = 16.sp
                                )
                            )
                            TextButton(onClick = { isEditingBio = !isEditingBio }) {
                                Text(if (isEditingBio) "Done" else "Edit", color = CalmSphereBlue)
                            }
                        }

                        if (isEditingBio) {
                            OutlinedTextField(
                                value = bioText,
                                onValueChange = { bioText = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = CalmLightShapes.Small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CalmEmerald,
                                    unfocusedBorderColor = CalmHairline
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val p = practitioner ?: return@launch
                                        specialistRepo.updateSpecialistProfile(
                                            practitionerId = p.id,
                                            fullName = p.fullName,
                                            title = p.title,
                                            bio = bioText,
                                            languages = p.languages,
                                            supportedTypes = p.supportedConsultationTypes
                                        )
                                        isEditingBio = false
                                    }
                                },
                                shape = CalmLightShapes.Pill,
                                colors = ButtonDefaults.buttonColors(containerColor = CalmInkNavy)
                            ) {
                                Text("Save Bio")
                            }
                        } else {
                            Text(
                                text = bioText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}
