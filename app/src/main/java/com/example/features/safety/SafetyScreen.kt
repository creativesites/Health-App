package com.example.features.safety

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.core.design.*
import com.example.core.model.SafetyResource
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.ui.theme.*

@Composable
fun SafetyScreen(
    onBack: () -> Unit
) {
    val safetyResources = remember { AppRepositoryLocator.safetyRepository.getSafetyResources() }
    var selectedResourceForCall by remember { mutableStateOf<SafetyResource?>(null) }

    if (selectedResourceForCall != null) {
        val resource = selectedResourceForCall!!
        AlertDialog(
            onDismissRequest = { selectedResourceForCall = null },
            shape = CalmLightShapes.Prominent,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    ),
                    color = CalmInkNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "Confidential Helpline: ${resource.contactNumber}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmCrisisCoral
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resource.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "In this demonstration build, phone dialing is simulated without making real cellular calls.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSoftSlate
                        )
                    )
                }
            },
            confirmButton = {
                CalmButton(
                    text = "Close",
                    onClick = { selectedResourceForCall = null }
                )
            }
        )
    }

    AuraBackground(aura = AuraType.Discovery) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = "Support & Safety",
                    subtitle = "Immediate guidance & 24/7 helplines",
                    onBack = onBack
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
                // Reassuring Hero Card (No sirens, pure comfort and dignity)
                CalmCard(
                    shape = CalmLightShapes.Hero,
                    backgroundColor = CalmWhite
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        TheSphere(size = 56.dp, isBreathing = true)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "You are not alone.",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 24.sp
                            ),
                            color = CalmInkNavy
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "If you are in immediate distress or danger, compassionate support is available right now.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Grounding Prompt
                Surface(
                    shape = CalmLightShapes.Standard,
                    color = CalmMistSurface,
                    border = BorderStroke(1.dp, CalmHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        TheSphere(size = 28.dp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Take a breath",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = "Give yourself a moment before deciding your next step.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Zambian Helplines & Resources",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )

                Spacer(modifier = Modifier.height(10.dp))

                safetyResources.forEach { resource ->
                    CalmCard(
                        shape = CalmLightShapes.Standard,
                        backgroundColor = CalmWhite,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .testTag("safety_resource_${resource.id}"),
                        onClick = { selectedResourceForCall = resource }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = resource.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = CalmInkNavy
                                    )
                                    if (resource.isCrisis24_7) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CalmLightShapes.Pill,
                                            color = CalmCrisisCoral.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = "24/7",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CalmCrisisCoral
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = resource.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Phone,
                                        contentDescription = null,
                                        tint = CalmCrisisCoral,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = resource.contactNumber,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmCrisisCoral
                                        )
                                    )
                                }
                            }

                            CalmButton(
                                text = "Call",
                                variant = CalmButtonVariant.Secondary,
                                onClick = { selectedResourceForCall = resource },
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Three-step Safety Sequence
                Text(
                    text = "Personal Safety Steps",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                val steps = listOf(
                    Triple("1", "Reach out to someone you trust", "A friend, family member, or care provider who listens without judgment."),
                    Triple("2", "Move to a secure, quiet space", "A calming environment with low sensory stimulation."),
                    Triple("3", "Practice grounding breath", "Four counts in, hold for four, release slowly for six.")
                )

                steps.forEach { (num, title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CalmSessionsAura,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = num,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = CalmInkNavy
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Healthcare Privacy Assurance Card
                CalmCard(
                    shape = CalmLightShapes.Prominent,
                    backgroundColor = CalmWhite
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_util_privacy),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Confidential & Protected",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                ),
                                color = CalmInkNavy
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your safety contacts, health journals, and emergency check-ins are encrypted locally and handled with strict patient confidentiality.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
