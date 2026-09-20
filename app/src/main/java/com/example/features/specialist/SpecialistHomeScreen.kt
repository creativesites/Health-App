package com.example.features.specialist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.*
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SpecialistHomeScreen(
    onNavigateToSchedule: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onStartConsultation: (appointmentId: String) -> Unit,
    onWriteEncounterNotes: (appointmentId: String) -> Unit,
    onOpenMessages: (patientId: String, patientName: String) -> Unit,
    onSwitchRole: () -> Unit = {}
) {
    val practitionerRepo = AppRepositoryLocator.practitionerRepository
    val appointmentRepo = AppRepositoryLocator.appointmentRepository
    val specialistRepo = AppRepositoryLocator.specialistRepository

    var practitioner by remember { mutableStateOf<Practitioner?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var patients by remember { mutableStateOf<List<SpecialistPatientSummary>>(emptyList()) }

    var selectedDayIndex by remember { mutableIntStateOf(3) } // Thursday (Today)
    var showMonthDropdown by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("September") }
    var showSwitchRoleDialog by remember { mutableStateOf(false) }

    // Intercept Back Press so Specialist NEVER navigates to Patient Home!
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
                    text = "Switch Account or Portal?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        color = CalmInkNavy
                    )
                )
            },
            text = {
                Text(
                    text = "You are currently in the Specialist Practice Workspace. Would you like to switch to the Patient Sanctuary or return to the role chooser?",
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
                    Text("Switch Role", color = CalmEmerald, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSwitchRoleDialog = false }) {
                    Text("Stay in Workspace", color = CalmSlate)
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        practitionerRepo.getPractitioners().collectLatest { list ->
            practitioner = list.find { it.id == "doc_chileshe_01" } ?: list.firstOrNull()
        }
    }

    LaunchedEffect(Unit) {
        appointmentRepo.getAppointments().collectLatest { list ->
            appointments = list.filter { it.practitionerId == "doc_chileshe_01" }
        }
    }

    LaunchedEffect(Unit) {
        specialistRepo.getSpecialistPatients("doc_chileshe_01").collectLatest { list ->
            patients = list
        }
    }

    val upcomingAppointments = appointments.filter { it.status == AppointmentStatus.CONFIRMED }
    val pendingAppointments = appointments.filter { it.status == AppointmentStatus.PENDING_PAYMENT }
    val completedAppointments = appointments.filter { it.status == AppointmentStatus.COMPLETED }

    // Slender capsule clinical rhythm (Mon to Sun) showing daily consultation load
    val weeklyClinicalRhythm = listOf(
        Triple("Mon", 0.70f, "3 consults completed"),
        Triple("Tue", 0.85f, "4 consults completed"),
        Triple("Wed", 0.55f, "2 consults completed"),
        Triple("Thu", 0.95f, "3 consults scheduled (Today)"),
        Triple("Fri", 0.65f, "2 consults scheduled"),
        Triple("Sat", 0.30f, "Weekend clinic slots"),
        Triple("Sun", 0.15f, "Practice rest day")
    )

    // Warm luminous aura wrapper matching Patient Home styling
    AuraBackground(
        aura = AuraType.Payments,
        secondaryAura = AuraType.Sessions,
        intensity = 1.15f
    ) {
        Scaffold(
            topBar = {
                // Header with floating circular and pill controls matching Patient Home
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
                        // Switch Role / Chooser Button
                        Surface(
                            onClick = { showSwitchRoleDialog = true },
                            shape = CircleShape,
                            color = CalmSessionsAura,
                            border = BorderStroke(1.dp, CalmEmerald.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .testTag("specialist_switch_role_button")
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapHoriz,
                                    contentDescription = "Switch Role / Account",
                                    tint = CalmEmerald,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Availability Quick Configuration
                        Surface(
                            onClick = onNavigateToAvailability,
                            shape = CircleShape,
                            color = CalmWhite,
                            border = BorderStroke(1.dp, CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .testTag("specialist_availability_button")
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Manage Availability",
                                    tint = CalmInkNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Specialist Avatar with HPCZ ring
                        Surface(
                            shape = CircleShape,
                            color = CalmSessionsAura,
                            border = BorderStroke(1.5.dp, CalmEmerald),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = practitioner?.avatarInitials ?: "MC",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = CalmEmerald,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting & Headline
                item {
                    val doctorFirstName = practitioner?.fullName ?: "Dr. Mutale"
                    Text(
                        text = "Welcome, $doctorFirstName",
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
                        text = "HPCZ-Licensed Clinical Practice • Woodlands, Lusaka",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 14.5.sp
                        )
                    )
                }

                // Hero Numerical Metrics & Month Dropdown (Matching Patient Home)
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${appointments.size.coerceAtLeast(14)}",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 34.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = CalmInkNavy
                                )
                                Text(
                                    text = " Consults",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp,
                                        color = CalmSlate
                                    ),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${patients.size.coerceAtLeast(4)}",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 34.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = CalmInkNavy
                                )
                                Text(
                                    text = " Patients",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp,
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
                }

                // Trio of Status Pill Chips (Golden Yellow, Matte Dark, Soft Muted)
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Golden Honey Pill (Next Slot)
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
                                    text = "Today: 10:00 CAT",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CalmInkNavy
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        // Matte Charcoal Pill (Therapy Type)
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
                                    text = "Therapy • Video Room",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = CalmWhite
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        // Soft Muted Pill (Patient Name)
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
                                    text = "Kondwani T.",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = CalmInkNavy
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // SHOWSTOPPING MATTE DARK CONTRAST CARD (Exact Match to Patient Home)
                item {
                    val nextApt = upcomingAppointments.firstOrNull() ?: appointments.firstOrNull()
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
                                        text = "Confirmed Clinical Session",
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
                                        text = if (nextApt?.consultationType == ConsultationType.IN_PERSON) "IN-PERSON CLINIC" else "ONLINE VIDEO",
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

                            Spacer(modifier = Modifier.height(18.dp))

                            // Middle section: Patient info + Medical Report card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = nextApt?.let { "${it.dateIso}, ${it.timeSlotLabel}" } ?: "Today, 14:00 CAT",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Patient: ${nextApt?.patientName ?: "Kondwani Tembo"}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmWhite,
                                            fontSize = 22.sp
                                        )
                                    )
                                    Text(
                                        text = nextApt?.serviceName ?: "Clinical Psychological Counselling",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            color = Color(0xFFC7CDD8),
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "K${nextApt?.priceZmw?.toInt() ?: 400} • ${nextApt?.paymentStatus?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Paid"} via Mobile Money",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmHoneyGold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Right: Crisp White "Clinical Intake / Sheet" preview
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = CalmWhite,
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .width(108.dp)
                                        .clickable {
                                            if (nextApt != null) onWriteEncounterNotes(nextApt.id)
                                        }
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
                                            text = "CLINICAL\nINTAKE",
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

                                        // Mini document lines
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(3.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth().height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                            Box(modifier = Modifier.fillMaxWidth(0.8f).height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                            Box(modifier = Modifier.fillMaxWidth(0.6f).height(2.5.dp).background(Color(0xFFE2E6EC), RoundedCornerShape(1.dp)))
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Circular note view pill button
                                        Surface(
                                            shape = CircleShape,
                                            color = CalmDarkMatte,
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Description,
                                                    contentDescription = "View intake note",
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
                                    val aptId = nextApt?.id ?: "apt_specialist_demo"
                                    onStartConsultation(aptId)
                                },
                                shape = CalmLightShapes.Pill,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CalmWhite,
                                    contentColor = CalmDarkMatte
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_specialist_enter_room")
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
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            color = CalmDarkMatte
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Slender Capsule Weekly Schedule (Matching Patient Home rhythm)
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
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
                                Column {
                                    Text(
                                        text = "Weekly Clinical Load",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = CalmInkNavy
                                        )
                                    )
                                    Text(
                                        text = weeklyClinicalRhythm[selectedDayIndex].third,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                Surface(
                                    shape = CalmLightShapes.Pill,
                                    color = CalmSessionsAura
                                ) {
                                    Text(
                                        text = "3 SLOTS TODAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmEmerald,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 7 Days Capsule Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                weeklyClinicalRhythm.forEachIndexed { index, (dayName, fillRatio, _) ->
                                    val isCurrentDay = index == selectedDayIndex
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable { selectedDayIndex = index }
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        // Vertical Capsule
                                        Box(
                                            modifier = Modifier
                                                .width(26.dp)
                                                .height(68.dp)
                                                .clip(RoundedCornerShape(13.dp))
                                                .background(if (isCurrentDay) CalmSessionsAura else CalmMistSurface),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(fillRatio)
                                                    .clip(RoundedCornerShape(13.dp))
                                                    .background(if (isCurrentDay) CalmEmerald else CalmSphereBlue.copy(alpha = 0.7f))
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = dayName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = if (isCurrentDay) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrentDay) CalmInkNavy else CalmSlate,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section: Practice Schedule List
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Practice Schedule",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            color = CalmInkNavy
                        )
                        TextButton(onClick = onNavigateToSchedule) {
                            Text(
                                text = "View all (${appointments.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmSphereBlue
                                )
                            )
                        }
                    }
                }

                if (appointments.isEmpty()) {
                    item {
                        CalmEmptyState(
                            title = "No appointments scheduled",
                            message = "Patient booking requests will appear here with instant approval controls.",
                            actionText = null
                        )
                    }
                } else {
                    items(appointments.take(3)) { apt ->
                        SpecialistAppointmentCompactCard(
                            appointment = apt,
                            onStartConsultation = { onStartConsultation(apt.id) },
                            onWriteEncounterNotes = { onWriteEncounterNotes(apt.id) }
                        )
                    }
                }

                // Section: Active Patients Roster
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Patients",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp
                            ),
                            color = CalmInkNavy
                        )
                        TextButton(onClick = onNavigateToPatients) {
                            Text(
                                text = "All Patients (${patients.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmSphereBlue
                                )
                            )
                        }
                    }
                }

                items(patients.take(2)) { patient ->
                    SpecialistPatientQuickCard(
                        patient = patient,
                        onOpenPatient = onNavigateToPatients,
                        onMessage = { onOpenMessages(patient.patientId, patient.displayName) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }
}

@Composable
private fun SpecialistAppointmentCompactCard(
    appointment: Appointment,
    onStartConsultation: () -> Unit,
    onWriteEncounterNotes: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CalmDiscoveryAura,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "KT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = CalmSphereBlue
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Kondwani Tembo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = CalmInkNavy
                        )
                        Text(
                            text = "${appointment.dateIso} • ${appointment.timeSlotLabel} CAT",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                ConsultationTypeChip(type = appointment.consultationType)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "K650 • Paid via Mobile Money",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = InterFontFamily,
                        color = CalmEmerald,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onWriteEncounterNotes,
                        shape = CalmLightShapes.Pill,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Button(
                        onClick = onStartConsultation,
                        shape = CalmLightShapes.Pill,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CalmDarkMatte,
                            contentColor = CalmWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Launch",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialistPatientQuickCard(
    patient: SpecialistPatientSummary,
    onOpenPatient: () -> Unit,
    onMessage: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenPatient)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = CircleShape,
                    color = CalmSessionsAura,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = patient.displayName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = CalmEmerald
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = patient.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = CalmInkNavy
                    )
                    Text(
                        text = patient.intakeSummary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            IconButton(
                onClick = onMessage,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CalmMistSurface)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Message Patient",
                    tint = CalmInkNavy,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
