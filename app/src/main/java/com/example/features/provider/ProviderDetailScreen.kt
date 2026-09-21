package com.example.features.provider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.core.model.ConsultationType
import com.example.ui.theme.*

@Composable
fun ProviderDetailScreen(
    viewModel: ProviderDetailViewModel,
    onBookAppointment: (providerId: String) -> Unit,
    onMessageProvider: (providerId: String, providerName: String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val practitioner = uiState.practitioner
    var isBookmarked by remember { mutableStateOf(false) }
    var selectedConsultationFormat by remember { mutableStateOf(ConsultationType.ONLINE) }

    AuraBackground(
        aura = AuraType.Discovery,
        secondaryAura = AuraType.Payments,
        intensity = 1.15f
    ) {
        Scaffold(
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
                        onClick = onBack,
                        shape = CircleShape,
                        color = CalmWhite.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .testTag("provider_detail_back_btn")
                            .size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CalmInkNavy,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmWhite.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, CalmHairline)
                    ) {
                        Text(
                            text = practitioner?.primarySpecialty?.displayName ?: "Specialist Profile",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmSphereBlue,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { isBookmarked = !isBookmarked },
                            shape = CircleShape,
                            color = CalmWhite.copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Save provider",
                                    tint = if (isBookmarked) CalmSphereBlue else CalmInkNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (practitioner != null) {
                    Surface(
                        color = CalmWhite,
                        shadowElevation = 12.dp,
                        border = BorderStroke(1.dp, CalmHairline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Consultation Fee",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 11.sp
                                    )
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "K${practitioner.startingPriceZmw.toInt()}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmInkNavy,
                                            fontSize = 24.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ZMW",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CalmButton(
                                    text = "Message",
                                    onClick = { onMessageProvider(practitioner.id, practitioner.fullName) },
                                    variant = CalmButtonVariant.Secondary,
                                    icon = Icons.Outlined.ChatBubbleOutline,
                                    modifier = Modifier.weight(1f).testTag("btn_message_provider")
                                )

                                CalmButton(
                                    text = "Book Session",
                                    onClick = { onBookAppointment(practitioner.id) },
                                    variant = CalmButtonVariant.Primary,
                                    icon = Icons.Default.CalendarToday,
                                    modifier = Modifier.weight(1.3f).testTag("btn_book_appointment")
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent  
        ) { padding ->
            if (practitioner == null) {
                CalmLoadingState(
                    message = "Loading practitioner details...",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    // Hero Specialist Card
                    CalmCard(
                        shape = CalmLightShapes.Hero,
                        backgroundColor = CalmWhite,
                        modifier = Modifier.testTag("provider_hero_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Large Avatar with online badge
                                Box {
                                    ProviderAvatar(
                                        initials = practitioner.avatarInitials,
                                        size = 84,
                                        backgroundColor = CalmDiscoveryAura,
                                        imageResId = practitioner.imageResId
                                    )
                                    // Live availability pulse badge
                                    Surface(
                                        shape = CircleShape,
                                        color = CalmEmerald,
                                        border = BorderStroke(2.dp, CalmWhite),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                    ) {}
                                }

                                Spacer(modifier = Modifier.width(18.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = practitioner.fullName,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 22.sp,
                                            letterSpacing = (-0.5).sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Text(
                                        text = practitioner.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSphereBlue,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        VerifiedBadge()
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Surface(
                                            shape = CalmLightShapes.Pill,
                                            color = CalmHoneyGoldLight.copy(alpha = 0.8f),
                                            border = BorderStroke(1.dp, CalmHoneyGold.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = CalmHoneyGoldDark,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "${practitioner.rating} (${practitioner.reviewCount} reviews)",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = InterFontFamily,
                                                        color = CalmHoneyGoldDark,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = CalmHairline)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Verification Body Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_util_verified_specialist),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = practitioner.verificationBody,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Specialist Highlights Metric Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProviderStatPill(
                            label = "Experience",
                            value = "10+ Yrs",
                            icon = Icons.Outlined.WorkspacePremium,
                            modifier = Modifier.weight(1f)
                        )
                        ProviderStatPill(
                            label = "Patients",
                            value = "850+ Consults",
                            icon = Icons.Outlined.PeopleOutline,
                            modifier = Modifier.weight(1f)
                        )
                        ProviderStatPill(
                            label = "Avg Response",
                            value = "< 15 Mins",
                            icon = Icons.Outlined.Speed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Consultation Format Options
                    Text(
                        text = "Consultation Modality",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val formats = listOf(
                            Pair(ConsultationType.ONLINE, "Virtual Video / Audio"),
                            Pair(ConsultationType.IN_PERSON, "In-Person Clinic Visit")
                        )

                        formats.forEach { (type, label) ->
                            val isSelected = selectedConsultationFormat == type
                            Surface(
                                shape = CalmLightShapes.Standard,
                                color = if (isSelected) CalmDiscoveryAura else CalmWhite,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CalmSphereBlue else CalmHairline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedConsultationFormat = type }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Icon(
                                        imageVector = if (type == ConsultationType.ONLINE) Icons.Outlined.VideoCall else Icons.Outlined.LocalHospital,
                                        contentDescription = null,
                                        tint = if (isSelected) CalmSphereBlue else CalmSlate,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) CalmSphereBlue else CalmInkNavy
                                        )
                                    )
                                    Text(
                                        text = if (type == ConsultationType.ONLINE) "HD Encrypted Room" else "Lusaka Physical Clinic",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bio & Clinical Philosophy
                    CalmCard(shape = CalmLightShapes.Standard, backgroundColor = CalmWhite) {
                        Text(
                            text = "About & Clinical Approach",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = CalmInkNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = practitioner.bio,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                lineHeight = 22.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Consultation Style",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = practitioner.consultationStyle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Languages Spoken",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmInkNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            practitioner.languages.forEach { lang ->
                                Surface(
                                    shape = CalmLightShapes.Pill,
                                    color = CalmDiscoveryAura.copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = lang,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSphereBlue,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Available Services
                    Text(
                        text = "Clinical Services & Pricing",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    uiState.services.forEach { service ->
                        CalmCard(
                            shape = CalmLightShapes.Standard,
                            backgroundColor = CalmWhite,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = service.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            lineHeight = 18.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            tint = CalmSphereBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${service.durationMinutes} minutes",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Surface(
                                    shape = CalmLightShapes.Pill,
                                    color = CalmDiscoveryAura
                                ) {
                                    Text(
                                        text = "K${service.priceZmw.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = CalmSphereBlue,
                                            fontSize = 16.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Location & Practice
                    if (practitioner.practice != null) {
                        CalmCard(
                            shape = CalmLightShapes.Standard,
                            backgroundColor = CalmWhite
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CalmDiscoveryAura),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalHospital,
                                        contentDescription = null,
                                        tint = CalmSphereBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Practice & Clinic Location",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Text(
                                        text = practitioner.practice.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSphereBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            practitioner.practice.location.physicalAddress?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                                )
                            }
                            Text(
                                text = "${practitioner.location.area}, ${practitioner.location.city}, Zambia",
                                style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
private fun ProviderStatPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CalmLightShapes.Standard,
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 0.5.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CalmSphereBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = CalmInkNavy
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    fontSize = 10.sp,
                    color = CalmSlate
                )
            )
        }
    }
}
