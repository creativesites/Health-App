package com.example.features.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.core.design.*
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onCompleteOnboarding: (city: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedCity by remember { mutableStateOf("Lusaka") }
    var patientName by remember { mutableStateOf("Kondwani Tembo") }
    var patientPhone by remember { mutableStateOf("+260 97 5543210") }
    var consentAgreed by remember { mutableStateOf(true) }

    val zambianCities = listOf("Lusaka", "Kitwe", "Ndola", "Livingstone", "Kabwe", "Chipata", "Solwezi")

    AuraBackground(aura = AuraType.Discovery) {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Step indicator
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(3) { index ->
                            Surface(
                                shape = CalmLightShapes.Pill,
                                color = if (step >= index + 1) CalmSphereBlue else CalmMistSurface,
                                border = BorderStroke(1.dp, if (step >= index + 1) CalmSphereBlue else CalmHairline),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(5.dp)
                                    .width(if (step == index + 1) 32.dp else 14.dp)
                            ) {}
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    when (step) {
                        1 -> {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ill_mental_wellbeing),
                                    contentDescription = "Mental Wellbeing Sanctuary",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Welcome to Calm Light",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 28.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = CalmInkNavy
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "A compassionate, tranquil sanctuary for mental health and verified healthcare consultations across Zambia.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    lineHeight = 24.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            CalmCard(shape = CalmLightShapes.Prominent) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_util_verified_specialist),
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Verified Zambian Practitioners",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = CalmInkNavy
                                        )
                                        Text(
                                            text = "Accredited with the Health Professions Council of Zambia (HPCZ).",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = CalmHairline)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_util_mobile_money),
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Mobile Money First",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = InterFontFamily,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = CalmInkNavy
                                        )
                                        Text(
                                            text = "Seamlessly pay with MTN Mobile Money, Airtel Money, or Zamtel.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ill_find_specialist),
                                    contentDescription = "Find Specialist",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Where are you based?",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 26.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = CalmInkNavy
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Select your city in Zambia to connect with nearby specialists and in-person clinics.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                zambianCities.forEach { city ->
                                    val isSelected = selectedCity == city
                                    Surface(
                                        shape = CalmLightShapes.Standard,
                                        color = if (isSelected) CalmDiscoveryAura else CalmWhite,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) CalmSphereBlue else CalmHairline
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedCity = city }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_util_location),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = city,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontFamily = InterFontFamily,
                                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                        color = if (isSelected) CalmSphereBlue else CalmInkNavy
                                                    )
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Outlined.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = CalmSphereBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ill_privacy),
                                    contentDescription = "Privacy & Confidentiality",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Your Profile & Consent",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 26.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = CalmInkNavy
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Encrypted, confidential clinical records protected under Zambian healthcare standards.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = patientName,
                                onValueChange = { patientName = it },
                                label = { Text("Full Name", color = CalmSlate) },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = CalmSlate) },
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
                                    .testTag("input_patient_name")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = patientPhone,
                                onValueChange = { patientPhone = it },
                                label = { Text("Mobile Number (Zambia)", color = CalmSlate) },
                                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = CalmSlate) },
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
                                    .testTag("input_patient_phone")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            CalmCard(
                                shape = CalmLightShapes.Standard,
                                backgroundColor = CalmWhite
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { consentAgreed = !consentAgreed }
                                ) {
                                    Checkbox(
                                        checked = consentAgreed,
                                        onCheckedChange = { consentAgreed = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CalmEmerald,
                                            uncheckedColor = CalmSlate
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_util_privacy),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Healthcare Privacy Consent",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = CalmInkNavy
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "I consent to confidential encrypted handling of my consultation scheduling, medical intake notes, and treatment encounters in accordance with healthcare privacy standards.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Buttons
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    CalmButton(
                        text = if (step == 3) "Enter Calm Light" else "Continue",
                        onClick = {
                            if (step < 3) {
                                step += 1
                            } else {
                                onCompleteOnboarding(selectedCity)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_next_button")
                    )

                    if (step > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { step -= 1 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Back",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
