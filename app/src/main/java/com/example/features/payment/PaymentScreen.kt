package com.example.features.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.PaymentProviderType
import com.example.core.model.PaymentStatus
import com.example.ui.theme.*

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onViewAppointment: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val appointment = uiState.appointment

    val providers = listOf(
        Pair(PaymentProviderType.MTN_MOMO, "MTN MoMo"),
        Pair(PaymentProviderType.AIRTEL_MONEY, "Airtel Money"),
        Pair(PaymentProviderType.ZAMTEL_KWACHA, "Zamtel Kwacha")
    )

    AuraBackground(aura = AuraType.Payments) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = if (uiState.paymentStatus == PaymentStatus.PAID) "Receipt" else "Complete Booking",
                    subtitle = "Zambia Mobile Money",
                    onBack = if (uiState.paymentStatus != PaymentStatus.PAID) onBack else null
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Gentle Sandbox Information
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmPaymentsAura.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color(0xFF8A5B15),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sandbox simulation: No live Kwacha will be deducted from your phone wallet.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = Color(0xFF6B450B),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.paymentStatus == PaymentStatus.PAID) {
                    // Success Receipt State
                    CalmCard(
                        shape = CalmLightShapes.Hero,
                        backgroundColor = CalmWhite
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CalmSessionsAura,
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Success",
                                        tint = CalmEmerald,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Payment Confirmed",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 24.sp
                                ),
                                color = CalmInkNavy
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Your session with ${appointment?.practitionerName} is secured.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    textAlign = TextAlign.Center,
                                    color = CalmSlate
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = CalmHairline)
                            Spacer(modifier = Modifier.height(16.dp))

                            ReceiptRow(label = "Total amount", value = "K${appointment?.priceZmw?.toInt() ?: 0}.00")
                            ReceiptRow(label = "Wallet", value = uiState.selectedProvider.displayName)
                            ReceiptRow(label = "Phone number", value = uiState.mobileNumber)
                            ReceiptRow(label = "Reference", value = uiState.completedTransaction?.referenceCode ?: "MOMO-91024")
                            ReceiptRow(label = "Date & Time", value = "${appointment?.dateIso} • ${appointment?.timeSlotLabel}")
                            ReceiptRow(label = "Format", value = appointment?.consultationType?.label ?: "Online")

                            Spacer(modifier = Modifier.height(26.dp))

                            CalmButton(
                                text = "View My Appointments",
                                onClick = onViewAppointment,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_view_confirmed_appointment"
                            )
                        }
                    }
                } else {
                    // Consultation Fee Summary
                    CalmCard(shape = CalmLightShapes.Standard) {
                        Text(
                            text = "Consultation Summary",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp
                            ),
                            color = CalmInkNavy
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = appointment?.serviceName ?: "Clinical Consultation",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = CalmInkNavy
                                )
                                Text(
                                    text = "${appointment?.practitionerTitle} ${appointment?.practitionerName}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                                )
                            }
                            Text(
                                text = "K${appointment?.priceZmw?.toInt() ?: 0}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    color = CalmSphereBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${appointment?.dateIso} • ${appointment?.timeSlotLabel}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Choose Mobile Money Network",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        providers.forEach { (provider, label) ->
                            val isSelected = uiState.selectedProvider == provider
                            Surface(
                                onClick = { viewModel.onSelectProvider(provider) },
                                shape = CalmLightShapes.Standard,
                                color = if (isSelected) CalmDiscoveryAura.copy(alpha = 0.5f) else CalmWhite,
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.PhoneIphone,
                                            contentDescription = null,
                                            tint = if (isSelected) CalmSphereBlue else CalmSlate,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color = CalmInkNavy
                                            )
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = CalmSphereBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Wallet Phone Number",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We'll prompt your phone to approve the payment. Keep this screen open.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.mobileNumber,
                        onValueChange = { viewModel.onMobileNumberChange(it) },
                        label = { Text("Zambian Phone Number (+260)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = null,
                                tint = CalmSlate
                            )
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CalmWhite,
                            unfocusedContainerColor = CalmWhite,
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_payment_phone")
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    if (uiState.paymentStatus == PaymentStatus.PROCESSING) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            TheSphere(size = 40.dp, isBreathing = true)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Awaiting PIN approval on your phone...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmInkNavy
                                )
                            )
                        }
                    } else {
                        CalmButton(
                            text = "Authorize K${appointment?.priceZmw?.toInt() ?: 0} via ${uiState.selectedProvider.displayName}",
                            onClick = { viewModel.submitMobileMoneyPayment(onPaymentSuccess = {}) },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_authorize_momo_payment"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = CalmSlate)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = CalmInkNavy
            )
        )
    }
}
