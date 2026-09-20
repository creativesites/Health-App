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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val calendarDates = listOf(
        Triple("Mon", "21", "2026-09-21"),
        Triple("Tue", "22", "2026-09-22"),
        Triple("Wed", "23", "2026-09-23"),
        Triple("Thu", "24", "2026-09-24"),
        Triple("Fri", "25", "2026-09-25"),
        Triple("Sat", "26", "2026-09-26")
    )

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
        Scaffold(
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
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
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
                Text(
                    text = "4. Select Date",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(calendarDates) { (dayName, dayNum, iso) ->
                        val isSelected = uiState.selectedDateIso == iso
                        CalmCalendarTile(
                            dayName = dayName,
                            dayNumber = dayNum,
                            isSelected = isSelected,
                            isAvailable = true,
                            onClick = { viewModel.onSelectDate(iso) }
                        )
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

                // Morning & Afternoon slots
                val morningSlots = uiState.availableTimeSlots.filter { it.timeLabel.contains("AM") }
                val afternoonSlots = uiState.availableTimeSlots.filter { it.timeLabel.contains("PM") }

                if (morningSlots.isNotEmpty()) {
                    Text(
                        text = "Morning",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        morningSlots.forEach { slot ->
                            CalmTimeSlotPill(
                                timeLabel = slot.timeLabel,
                                isSelected = uiState.selectedTimeSlot?.id == slot.id,
                                isAvailable = slot.isAvailable,
                                onClick = { viewModel.onSelectTimeSlot(slot) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        afternoonSlots.forEach { slot ->
                            CalmTimeSlotPill(
                                timeLabel = slot.timeLabel,
                                isSelected = uiState.selectedTimeSlot?.id == slot.id,
                                isAvailable = slot.isAvailable,
                                onClick = { viewModel.onSelectTimeSlot(slot) },
                                modifier = Modifier.weight(1f)
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
