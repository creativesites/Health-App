package com.example.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.AppointmentStatus
import com.example.core.model.ConsultationType
import com.example.core.model.SpecialtyCategory
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDiscover: (specialty: SpecialtyCategory?) -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToCare: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onJoinConsultation: (appointmentId: String) -> Unit,
    onOpenMessaging: (conversationId: String, practitionerName: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onSwitchRole: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMood by remember { mutableStateOf<String?>("Grounded") }
    var selectedDayIndex by remember { mutableIntStateOf(3) } // Thursday (Today)
    var showMonthDropdown by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("September") }
    var showSwitchRoleDialog by remember { mutableStateOf(false) }

    // Intercept Back Press so Patient Sanctuary NEVER navigates into Specialist Workspace
    BackHandler(enabled = true) {
        showSwitchRoleDialog = true
    }

    if (showSwitchRoleDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchRoleDialog = false },
            shape = CalmLightShapes.Standard,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Switch Account or Role?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        color = CalmInkNavy
                    )
                )
            },
            text = {
                Text(
                    text = "Would you like to return to the role chooser to switch between Patient and Specialist workspaces?",
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
                        showSwitchRoleDialog = false
                        onSwitchRole()
                    }
                ) {
                    Text("Switch Role / Portal", color = CalmSphereBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSwitchRoleDialog = false }) {
                    Text("Stay in Sanctuary", color = CalmSlate)
                }
            }
        )
    }

    val moodOptions = listOf("Grounded", "Steady", "Tender", "Seeking clarity", "Restless")

    // Slender capsule rhythm data (Mon to Sun) inspired by the attached mockup
    val weeklyRhythm = listOf(
        Triple("Mon", 0.65f, "Steady grounding recorded"),
        Triple("Tue", 0.85f, "Deep breathing completed"),
        Triple("Wed", 0.50f, "Tender moment, took a walk"),
        Triple("Thu", 0.95f, "Grounded & alert (Today)"),
        Triple("Fri", 0.70f, "Consultation preparation"),
        Triple("Sat", 0.40f, "Rest & recovery"),
        Triple("Sun", 0.60f, "Mindful evening reflection")
    )

    // Warm luminous aura wrapper following specified rules (Warm payments/amber base + celestial discovery wash)
    AuraBackground(
        aura = AuraType.Payments,
        secondaryAura = AuraType.Discovery,
        intensity = 1.15f
    ) {
        Scaffold(
            topBar = {
                // Header with floating circular and pill controls inspired by the attached design
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Left: Connection & Location Pill Chip
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
                                text = "Lusaka, Zambia",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    // Right: Floating Circular Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Discreet crisis shield
                        Surface(
                            onClick = onNavigateToSafety,
                            shape = CircleShape,
                            color = CalmCrisisCoralSoft,
                            border = BorderStroke(1.dp, CalmCrisisCoral.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .testTag("home_safety_button")
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = "Crisis support",
                                    tint = CalmCrisisCoral,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Notification Bell
                        Surface(
                            onClick = onNavigateToNotifications,
                            shape = CircleShape,
                            color = CalmWhite,
                            border = BorderStroke(1.dp, CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .testTag("home_notifications_button")
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.unreadNotificationsCount > 0) {
                                            Badge(containerColor = CalmSphereBlue) {
                                                Text("${uiState.unreadNotificationsCount}", color = CalmWhite, fontSize = 10.sp)
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
                        }

                        // Switch Role / Portal Button
                        Surface(
                            onClick = { showSwitchRoleDialog = true },
                            shape = CircleShape,
                            color = CalmDiscoveryAura,
                            border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .testTag("home_switch_role_button")
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapHoriz,
                                    contentDescription = "Switch Role",
                                    tint = CalmSphereBlue,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Patient Avatar
                        PatientAvatar(
                            patient = uiState.patient,
                            size = 40,
                            onClick = onNavigateToProfile
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                val patientFirstName = uiState.patient?.fullName?.substringBefore(" ") ?: "Kondwani"

                // Display Greeting
                Text(
                    text = "Welcome, $patientFirstName",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.sp,
                        letterSpacing = (-0.7).sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Your mental health sanctuary & clinical care circle.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate,
                        fontSize = 14.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Hero Numerical Metrics & Month Dropdown (Matching Screen 3 in mockup)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "14",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 34.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = " Days",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    color = CalmSlate
                                ),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "2",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 34.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = " Sessions",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    color = CalmSlate
                                ),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    // Month Selector Dropdown Pill
                    Box {
                        Surface(
                            onClick = { showMonthDropdown = !showMonthDropdown },
                            shape = CalmLightShapes.Pill,
                            color = CalmWhite,
                            border = BorderStroke(1.dp, CalmHairline),
                            shadowElevation = 0.5.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = selectedMonth,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = CalmInkNavy
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select month",
                                    tint = CalmSlate,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMonthDropdown,
                            onDismissRequest = { showMonthDropdown = false }
                        ) {
                            listOf("September", "October", "November").forEach { month ->
                                DropdownMenuItem(
                                    text = { Text(month) },
                                    onClick = {
                                        selectedMonth = month
                                        showMonthDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Trio of Status Pill Chips (Golden Yellow, Matte Dark, Soft Muted)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Golden Honey Pill (Pill 1)
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmHoneyGold,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "Upcoming: 24 Sep",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = CalmInkNavy
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Matte Charcoal Pill (Pill 2)
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmDarkMatte,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "Therapy • 10:00 CAT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = CalmWhite
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Soft Muted Pill (Pill 3)
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = Color(0xFFE9E4D8),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "Dr. Mwansa",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = CalmInkNavy
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SHOWSTOPPING MATTE DARK CONTRAST CARD (Inspired by the Medical Report card in Screen 3)
                val upcoming = uiState.upcomingAppointment
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = CalmDarkMatte,
                    border = BorderStroke(1.dp, CalmDarkMatteBorder),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        // Top status row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(CalmEmerald)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confirmed Session",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmWhite,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            // Clean badge pill
                            Surface(
                                shape = CalmLightShapes.Pill,
                                color = Color(0xFF2A2E36)
                            ) {
                                Text(
                                    text = "ONLINE VIDEO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = CalmEmerald,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Two column layout: Left details, Right Medical Report Sheet preview
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left Information Column
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = upcoming?.practitionerName ?: "Dr. Mwansa Chileshe",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmWhite,
                                        fontSize = 20.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = upcoming?.practitionerTitle ?: "Clinical Psychologist • HPCZ Accredited",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFFA6B0C0),
                                        fontSize = 12.5.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = upcoming?.intakeNotes?.let { "Reason: $it" } ?: "Pre-consultation clinical intake submitted. Safe video room ready.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = Color(0xFF8E99A8),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    ),
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = upcoming?.let { "${it.dateIso} • ${it.timeSlotLabel} CAT" } ?: "Tomorrow • 14:00 CAT",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmWhite,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "K${upcoming?.priceZmw?.toInt() ?: 400} • ${upcoming?.paymentStatus?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Paid"} via Mobile Money",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmHoneyGold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Right: Crisp White "Medical Report / Clinical Sheet" preview
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CalmWhite,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .width(108.dp)
                                    .clickable { onNavigateToAppointments() }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    // Red Cross symbol
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CalmCrisisCoralSoft,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = CalmCrisisCoral,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "CLINICAL\nREPORT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 11.sp
                                        ),
                                        color = CalmInkNavy
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Mini document placeholder lines
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth().height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                        Box(modifier = Modifier.fillMaxWidth(0.8f).height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                        Box(modifier = Modifier.fillMaxWidth(0.6f).height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Circular download/view pill button
                                    Surface(
                                        shape = CircleShape,
                                        color = CalmDarkMatte,
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDownward,
                                                contentDescription = "View medical note",
                                                tint = CalmWhite,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Full width CTA action button
                        Button(
                            onClick = {
                                val id = upcoming?.id ?: "apt_confirmed_1"
                                onJoinConsultation(id)
                            },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CalmWhite,
                                contentColor = CalmDarkMatte
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_join_consultation")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Videocam,
                                    contentDescription = null,
                                    tint = CalmDarkMatte,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enter Consultation Room",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = CalmDarkMatte
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // WEEKLY GROUNDING RHYTHM (Slender Capsule Bar Visualizer inspired by Screen 2 & 3)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Weekly Grounding Rhythm",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    ),
                                    color = CalmInkNavy
                                )
                                Text(
                                    text = "Daily emotional balance & mindfulness pace",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }

                            TheSphere(size = 30.dp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 7 Slender vertical capsule bars (Mon to Sun)
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(76.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            weeklyRhythm.forEachIndexed { index, (dayLabel, fillFraction, _) ->
                                val isSelected = selectedDayIndex == index
                                val isToday = index == 3

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { selectedDayIndex = index }
                                        .padding(horizontal = 4.dp)
                                ) {
                                    // Slender vertical capsule bar
                                    Box(
                                        modifier = Modifier
                                            .width(13.dp)
                                            .height(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEDE9DF)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(fillFraction)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isToday) CalmEmerald
                                                    else if (isSelected) CalmHoneyGold
                                                    else Color(0xFFFCD355)
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = dayLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isToday) CalmEmerald else if (isSelected) CalmInkNavy else CalmSlate,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Active Day Status Note
                        Surface(
                            shape = CalmLightShapes.Small,
                            color = CalmMistSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CalmEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${weeklyRhythm[selectedDayIndex].first}: ${weeklyRhythm[selectedDayIndex].third}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.5.sp,
                                        color = CalmInkNavy
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // WARM HONEY GOLD GROUNDING CARD (Inspired by the Birthday card in Screen 2)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CalmHoneyGold,
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Spa,
                                    contentDescription = null,
                                    tint = CalmInkNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TODAY'S GROUNDING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        fontSize = 11.sp,
                                        color = CalmInkNavy
                                    )
                                )
                            }

                            Text(
                                text = "24 Sep",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CalmInkNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Steady & Present",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 26.sp,
                                letterSpacing = (-0.3).sp,
                                color = CalmInkNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "How does your body and mind feel right now?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                color = CalmInkNavy.copy(alpha = 0.85f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mood selection pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(moodOptions) { mood ->
                                val isSelected = selectedMood == mood
                                Surface(
                                    onClick = {
                                        selectedMood = mood
                                        viewModel.recordQuickMood(mood)
                                    },
                                    shape = CalmLightShapes.Pill,
                                    color = if (isSelected) CalmDarkMatte else CalmWhite.copy(alpha = 0.85f),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    ) {
                                        Text(
                                            text = mood,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) CalmWhite else CalmInkNavy,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CLINICAL DOCUMENTS & FOLLOW-UP CAPSULES (Inspired by the Contract/Resume pills in Screen 2)
                Text(
                    text = "Clinical Records & Files",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Document Capsule 1: Clinical Intake (Blue icon)
                    Surface(
                        onClick = onNavigateToAppointments,
                        shape = RoundedCornerShape(20.dp),
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 0.5.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            // Squircle Icon
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF2E66B4),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "W",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmWhite,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Intake Form",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmInkNavy,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "1.8 MB",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CalmSlate,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            // Circular download icon
                            Surface(
                                shape = CircleShape,
                                color = CalmMistSurface,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = CalmInkNavy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Document Capsule 2: Care Summary / Prescription (Amber/Coral icon)
                    Surface(
                        onClick = onNavigateToAppointments,
                        shape = RoundedCornerShape(20.dp),
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 0.5.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            // Squircle Icon
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE26A3A),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Rx",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmWhite,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Care Plan",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalmInkNavy,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "740 KB",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CalmSlate,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            // Circular download icon
                            Surface(
                                shape = CircleShape,
                                color = CalmMistSurface,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = CalmInkNavy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CARE & SUPPORT ACTIONS
                Text(
                    text = "Care & Support",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    CalmCareTile(
                        title = "Talk with someone",
                        subtitle = "HPCZ verified doctors",
                        icon = Icons.Outlined.Psychology,
                        tintAura = CalmDiscoveryAura,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDiscover(null) }
                    )
                    CalmCareTile(
                        title = "Grounding exercises",
                        subtitle = "Breathing rhythm & focus",
                        icon = Icons.Outlined.SelfImprovement,
                        tintAura = CalmSessionsAura,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCare
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    CalmCareTile(
                        title = "Care journal",
                        subtitle = "Reflect & express thoughts",
                        icon = Icons.Outlined.AutoStories,
                        tintAura = CalmSelfCareAura,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCare
                    )
                    CalmCareTile(
                        title = "Doctor messages",
                        subtitle = "Follow-up notes & chat",
                        icon = Icons.Outlined.ChatBubbleOutline,
                        tintAura = CalmPaymentsAura,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenMessaging("conv_demo_1", "Dr. Mwansa Chileshe") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ZAMBIAN SPECIALTIES DISCOVERY CAROUSEL
                Text(
                    text = "Accredited Specialties",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                val specialtyShortcuts = listOf(
                    Triple(SpecialtyCategory.MENTAL_HEALTH, "Psychology & Therapy", Icons.Outlined.Psychology),
                    Triple(SpecialtyCategory.GENERAL_PRACTICE, "General Practice", Icons.Outlined.MedicalServices),
                    Triple(SpecialtyCategory.NUTRITION, "Diet & Nutrition", Icons.Outlined.Spa),
                    Triple(SpecialtyCategory.DENTAL, "Oral Health", Icons.Outlined.HealthAndSafety),
                    Triple(SpecialtyCategory.PHYSIOTHERAPY, "Physiotherapy", Icons.Outlined.FitnessCenter)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(specialtyShortcuts) { (specialty, label, icon) ->
                        Surface(
                            onClick = { onNavigateToDiscover(specialty) },
                            shape = CalmLightShapes.Pill,
                            color = CalmWhite,
                            border = BorderStroke(1.dp, CalmHairline),
                            shadowElevation = 0.5.dp,
                            modifier = Modifier.height(42.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = CalmSphereBlue,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = CalmInkNavy
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DISCREET 24/7 CRISIS SUPPORT
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmCrisisCoral.copy(alpha = 0.2f)),
                    shadowElevation = 0.5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSafety() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CalmCrisisCoralSoft,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = CalmCrisisCoral,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "In immediate crisis?",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Lifeline Zambia 933 is toll-free & 24/7 confidential.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = CalmSoftSlate,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun CalmCareTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tintAura: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = CircleShape,
                color = tintAura,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CalmInkNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = CalmInkNavy
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CalmSlate,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}
