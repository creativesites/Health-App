package com.example.features.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.core.design.*
import com.example.core.model.ConsultationType
import com.example.core.model.Practitioner
import com.example.ui.theme.*

@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onProceedToPayment: (appointmentId: String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val practitioner = uiState.practitioner

    // Confirmation Modal Dialog
    if (uiState.showConfirmationDialog && practitioner != null && uiState.selectedService != null && uiState.selectedTimeSlot != null) {
        CalmConfirmationDialog(
            practitionerName = practitioner.fullName,
            specialtyName = practitioner.primarySpecialty.displayName,
            serviceName = uiState.selectedService?.name ?: "Consultation",
            dateFormatted = uiState.selectedDateIso,
            timeFormatted = uiState.selectedTimeSlot?.timeLabel ?: "Selected Time",
            consultationType = uiState.selectedConsultationType,
            feeZmw = uiState.selectedService?.priceZmw ?: practitioner.startingPriceZmw,
            onConfirm = {
                viewModel.confirmBooking(onSuccess = onProceedToPayment)
            },
            onDismiss = {
                viewModel.onShowConfirmationDialog(false)
            }
        )
    }

    AuraBackground(aura = AuraType.Sessions) {
        // Editorial background image with subtle calm translucency
        Image(
            painter = painterResource(id = R.drawable.bg_editorial_booking),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.08f,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CalmTopBar(
                    title = "Find your time",
                    subtitle = "Choose a rhythm that suits you",
                    onBack = onBack
                )
            },
            bottomBar = {
                Surface(
                    color = CalmWhite,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, CalmHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Consultation fee",
                                style = MaterialTheme.typography.bodySmall,
                                color = CalmSlate
                            )
                            Text(
                                text = "K${uiState.selectedService?.priceZmw?.toInt() ?: practitioner?.startingPriceZmw?.toInt() ?: 0}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 24.sp,
                                    color = CalmInkNavy
                                )
                            )
                        }

                        CalmButton(
                            text = "Review & Confirm",
                            onClick = {
                                viewModel.onShowConfirmationDialog(true)
                            },
                            enabled = !uiState.isSubmitting && uiState.selectedService != null && uiState.selectedTimeSlot != null,
                            isLoading = uiState.isSubmitting,
                            testTag = "btn_proceed_to_payment"
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Hero Consultation Illustration Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ill_book_consultation),
                        contentDescription = "Book Healthcare Consultation",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Choose Doctor / Healthcare Professional
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "1. Healthcare Professional",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    Text(
                        text = "${uiState.allPractitioners.size} available",
                        style = MaterialTheme.typography.labelSmall.copy(color = CalmSlate)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Doctor Selection Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.allPractitioners) { doctor ->
                        val isSelected = doctor.id == practitioner?.id
                        Surface(
                            onClick = { viewModel.onSelectPractitioner(doctor) },
                            shape = CalmLightShapes.Standard,
                            color = if (isSelected) CalmDiscoveryAura.copy(alpha = 0.5f) else CalmWhite,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) CalmSphereBlue else CalmHairline
                            ),
                            modifier = Modifier
                                .width(220.dp)
                                .testTag("doctor_card_${doctor.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ProviderAvatar(
                                        initials = doctor.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                        size = 40,
                                        backgroundColor = if (isSelected) CalmSphereBlue.copy(alpha = 0.15f) else CalmMistSurface,
                                        imageResId = doctor.imageResId
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doctor.fullName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            ),
                                            color = CalmInkNavy,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = doctor.primarySpecialty.displayName,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = CalmSlate,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${doctor.location.city} • Verified",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CalmSoftSlate,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "K${doctor.startingPriceZmw.toInt()}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            color = CalmSphereBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Choose Service
                Text(
                    text = "2. Consultation Service",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.services.forEach { service ->
                        val isSelected = uiState.selectedService?.id == service.id
                        Surface(
                            onClick = { viewModel.onSelectService(service) },
                            shape = CalmLightShapes.Standard,
                            color = if (isSelected) CalmDiscoveryAura.copy(alpha = 0.45f) else CalmWhite,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) CalmSphereBlue else CalmHairline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${service.durationMinutes} mins • ${service.description}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CalmSlate,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "K${service.priceZmw.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 18.sp,
                                        color = CalmInkNavy
                                    ),
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Consultation Format Toggle
                Text(
                    text = "3. Format",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isOnline = uiState.selectedConsultationType == ConsultationType.ONLINE
                    Surface(
                        onClick = { viewModel.onSelectConsultationType(ConsultationType.ONLINE) },
                        shape = CalmLightShapes.Standard,
                        color = if (isOnline) CalmDiscoveryAura.copy(alpha = 0.6f) else CalmWhite,
                        border = BorderStroke(1.5.dp, if (isOnline) CalmSphereBlue else CalmHairline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Videocam,
                                contentDescription = null,
                                tint = if (isOnline) CalmSphereBlue else CalmSlate,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Online video/audio",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = "Audio-first fallback enabled",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    val isInPerson = uiState.selectedConsultationType == ConsultationType.IN_PERSON
                    Surface(
                        onClick = { viewModel.onSelectConsultationType(ConsultationType.IN_PERSON) },
                        shape = CalmLightShapes.Standard,
                        color = if (isInPerson) CalmDiscoveryAura.copy(alpha = 0.6f) else CalmWhite,
                        border = BorderStroke(1.5.dp, if (isInPerson) CalmSphereBlue else CalmHairline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = if (isInPerson) CalmSphereBlue else CalmSlate,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "In-person clinic",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = "${practitioner?.location?.area ?: "Clinic"}, ${practitioner?.location?.city ?: ""}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 4: Calendar Date Selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "4. Select Date",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    
                    // Month Navigation Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.onMonthChange(uiState.visibleMonth.minusMonths(1)) },
                            enabled = !uiState.visibleMonth.isBefore(java.time.YearMonth.now())
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month", tint = if (!uiState.visibleMonth.isBefore(java.time.YearMonth.now())) CalmInkNavy else CalmHairline)
                        }
                        Text(
                            text = "${uiState.visibleMonth.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())} ${uiState.visibleMonth.year}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy
                            ),
                            modifier = Modifier.widthIn(min = 100.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { viewModel.onMonthChange(uiState.visibleMonth.plusMonths(1)) }
                        ) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Next Month", tint = CalmInkNavy)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Calendar Grid
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Days of week header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = CalmSlate
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val firstDayOfWeek = uiState.visibleMonth.atDay(1).dayOfWeek.value // 1 (Mon) to 7 (Sun)
                    val daysInMonth = uiState.visibleMonth.lengthOfMonth()
                    val totalCells = firstDayOfWeek - 1 + daysInMonth
                    val rows = Math.ceil(totalCells / 7.0).toInt()
                    
                    var currentDay = 1
                    
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 1..7) {
                                if (row == 0 && col < firstDayOfWeek) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else if (currentDay <= daysInMonth) {
                                    val date = uiState.visibleMonth.atDay(currentDay)
                                    val isPast = date.isBefore(java.time.LocalDate.now())
                                    val isAvailable = uiState.availableDates.contains(date.toString())
                                    val isSelected = uiState.selectedDateIso == date.toString()
                                    
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) CalmSphereBlue else if (isAvailable && !isPast) CalmDiscoveryAura.copy(alpha = 0.3f) else Color.Transparent)
                                            .clickable(enabled = isAvailable && !isPast) {
                                                viewModel.onSelectDate(date.toString())
                                            }
                                    ) {
                                        Text(
                                            text = currentDay.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) CalmWhite else if (isAvailable && !isPast) CalmInkNavy else CalmHairline,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }
                                    currentDay++
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 5: Available Time Slots
                Text(
                    text = "5. Available Time Slots",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Time slots logic using 24h split
                val morningSlots = uiState.availableTimeSlots.filter { 
                    try { 
                        val startHour = it.timeLabel.substringBefore(":").toInt(); startHour < 12 
                    } catch(e: Exception) { false }
                }
                val afternoonSlots = uiState.availableTimeSlots.filter { 
                    try { 
                        val startHour = it.timeLabel.substringBefore(":").toInt(); startHour >= 12 
                    } catch(e: Exception) { false }
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp),
                        color = CalmSphereBlue
                    )
                } else if (uiState.availableTimeSlots.isEmpty()) {
                    Surface(
                        shape = CalmLightShapes.Standard,
                        color = CalmWhite.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, CalmHairline),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "No open slots remaining for this date. Please choose another available date above.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 13.5.sp
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    if (morningSlots.isNotEmpty()) {
                        Text(
                            text = "Morning",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(morningSlots) { slot ->
                                CalmTimeSlotPill(
                                    timeLabel = slot.timeLabel,
                                    isSelected = uiState.selectedTimeSlot?.id == slot.id,
                                    isAvailable = slot.isAvailable,
                                    onClick = { viewModel.onSelectTimeSlot(slot) },
                                    modifier = Modifier.width(102.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (afternoonSlots.isNotEmpty()) {
                        Text(
                            text = "Afternoon",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(afternoonSlots) { slot ->
                                CalmTimeSlotPill(
                                    timeLabel = slot.timeLabel,
                                    isSelected = uiState.selectedTimeSlot?.id == slot.id,
                                    isAvailable = slot.isAvailable,
                                    onClick = { viewModel.onSelectTimeSlot(slot) },
                                    modifier = Modifier.width(102.dp)
                                )
                            }
                        }
                    }
                }
                
                if (uiState.conflictState) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = CalmLightShapes.Standard,
                        color = CalmCrisisCoralSoft,
                        border = BorderStroke(1.dp, CalmCrisisCoral.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CalmCrisisCoral)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = uiState.errorMessage ?: "The selected time slot is no longer available. Please choose another.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = CalmInkNavy)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 6: Consultation Intake Notes
                Text(
                    text = "6. Intake notes (Optional)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Anything you would like your practitioner to know beforehand.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.intakeNotes,
                    onValueChange = { viewModel.onIntakeNotesChange(it) },
                    placeholder = {
                        Text(
                            "e.g., General reflection, follow-up on sleep, feeling overwhelmed recently...",
                            style = MaterialTheme.typography.bodySmall.copy(color = CalmSoftSlate)
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    shape = CalmLightShapes.Standard,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CalmWhite,
                        unfocusedContainerColor = CalmWhite,
                        focusedBorderColor = CalmSphereBlue,
                        unfocusedBorderColor = CalmHairline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_intake_notes")
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
