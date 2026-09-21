package com.example.features.specialist

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.core.model.SpecialistAvailabilityRule
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SpecialistAvailabilityScreen(
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepo = AppRepositoryLocator.authRepository
    val availabilityRepo = AppRepositoryLocator.availabilityRepository
    val session by authRepo.getActiveSession().collectAsState(initial = null)
    val practitionerId = session?.practitionerId ?: "doc_chileshe_01"

    var availabilityRules by remember { mutableStateOf<List<SpecialistAvailabilityRule>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(practitionerId) {
        availabilityRepo.getAvailabilityRules(practitionerId).collectLatest { list ->
            availabilityRules = list
        }
    }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    AuraBackground(
        aura = AuraType.Payments,
        secondaryAura = AuraType.Sessions,
        intensity = 1.15f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CalmWhite)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CalmInkNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

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
                                text = "Woodlands Clinic • Lusaka",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "Consultation Availability",
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
                            text = "Configure working hours, session durations & visit types",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                item {
                    // Explanatory Banner
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 0.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CalmDiscoveryAura,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = CalmSphereBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Patients can only book sessions during active slots. Changes reflect across the patient search and booking experience immediately.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Weekly Practice Hours",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(daysOfWeek) { dayName ->
                    val rule = availabilityRules.firstOrNull { it.dayOfWeek.equals(dayName, ignoreCase = true) }
                        ?: SpecialistAvailabilityRule(
                            id = "rule_${practitionerId}_${dayName.lowercase()}",
                            practitionerId = practitionerId,
                            dayOfWeek = dayName,
                            startTime = "09:00",
                            endTime = "17:00",
                            slotDurationMinutes = 30,
                            bufferMinutes = 10,
                            enabled = false
                        )
                        
                    AvailabilityRuleCard(
                        rule = rule,
                        onToggleEnabled = { isEnabled ->
                            val updated = rule.copy(enabled = isEnabled)
                            coroutineScope.launch {
                                availabilityRepo.saveRule(updated)
                                snackbarHostState.showSnackbar("$dayName updated")
                            }
                        },
                        onToggleOnline = { allowsOnline ->
                            val updated = rule.copy(allowsOnline = allowsOnline)
                            coroutineScope.launch { availabilityRepo.saveRule(updated) }
                        },
                        onToggleInPerson = { allowsInPerson ->
                            val updated = rule.copy(allowsInPerson = allowsInPerson)
                            coroutineScope.launch { availabilityRepo.saveRule(updated) }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun AvailabilityRuleCard(
    rule: SpecialistAvailabilityRule,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    onToggleInPerson: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CalmWhite,
        border = BorderStroke(1.dp, if (rule.enabled) CalmSessionsAura else CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = rule.dayOfWeek,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = if (rule.enabled) CalmInkNavy else CalmSoftSlate,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = if (rule.enabled) "${rule.startTime} - ${rule.endTime} (${rule.slotDurationMinutes} min sessions, ${rule.bufferMinutes}m buffer)" else "Unavailable / Day off",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = if (rule.enabled) CalmSlate else CalmSoftSlate,
                            fontSize = 12.sp
                        )
                    )
                }

                Switch(
                    checked = rule.enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CalmWhite,
                        checkedTrackColor = CalmEmerald
                    ),
                    modifier = Modifier.testTag("switch_day_${rule.dayOfWeek.lowercase()}")
                )
            }

            if (rule.enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CalmHairline)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Online toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleOnline(!rule.allowsOnline) }
                    ) {
                        Checkbox(
                            checked = rule.allowsOnline,
                            onCheckedChange = onToggleOnline,
                            colors = CheckboxDefaults.colors(checkedColor = CalmSphereBlue)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Online Video",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmInkNavy,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // In-Person toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleInPerson(!rule.allowsInPerson) }
                    ) {
                        Checkbox(
                            checked = rule.allowsInPerson,
                            onCheckedChange = onToggleInPerson,
                            colors = CheckboxDefaults.colors(checkedColor = CalmEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "In-Person Clinic",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmInkNavy,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
