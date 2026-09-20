package com.example.features.discovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.*
import com.example.core.model.ConsultationType
import com.example.core.model.Practitioner
import com.example.core.model.SpecialtyCategory
import com.example.ui.theme.*

@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel,
    onSelectProvider: (providerId: String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val cities = listOf("Lusaka", "Kitwe", "Ndola", "Livingstone", "Kabwe")
    val specialties = SpecialtyCategory.values()

    // Screen wrapped in performant AuraBackground using Discovery palette with gentle golden warmth
    AuraBackground(
        aura = AuraType.Discovery,
        secondaryAura = AuraType.Payments,
        intensity = 1.15f
    ) {
        Scaffold(
            topBar = {
                // Header with floating circular back button, title pill, and reset action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Left: Back button in soft pill/circle
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = CalmWhite.copy(alpha = 0.94f),
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CalmInkNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Center: Discovery Badge Pill
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmWhite.copy(alpha = 0.94f),
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CalmSphereBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified Specialists",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    // Right: Reset button or Filter indicator
                    val hasActiveFilters = uiState.selectedSpecialty != null ||
                            uiState.selectedCity != null ||
                            uiState.selectedConsultationType != null ||
                            uiState.searchQuery.isNotEmpty()

                    Surface(
                        onClick = { if (hasActiveFilters) viewModel.clearFilters() },
                        shape = CircleShape,
                        color = if (hasActiveFilters) CalmHoneyGoldSoft else CalmWhite.copy(alpha = 0.94f),
                        border = BorderStroke(1.dp, if (hasActiveFilters) CalmHoneyGold.copy(alpha = 0.5f) else CalmHairline),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (hasActiveFilters) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterAltOff,
                                    contentDescription = "Reset Filters",
                                    tint = CalmInkNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Filters",
                                    tint = CalmSlate,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Editorial Title & Search Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Find Specialist Care",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 28.sp,
                            lineHeight = 34.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HPCZ-verified doctors, psychologists & therapists in Zambia",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate,
                            fontSize = 13.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // High-Contrast Search Field with Warm Shadow
                Surface(
                    shape = CalmLightShapes.Pill,
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = CalmSphereBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = {
                                Text(
                                    "Search by name, specialty, or clinic...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSoftSlate,
                                        fontSize = 13.5.sp
                                    )
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(
                                            Icons.Outlined.Close,
                                            contentDescription = "Clear",
                                            tint = CalmSlate,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("discovery_search_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Specialty Filter Pills (Styled like HomeScreen Trio Pills)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = uiState.selectedSpecialty == null
                        Surface(
                            onClick = { viewModel.onSpecialtySelected(null) },
                            shape = CalmLightShapes.Pill,
                            color = if (isAllSelected) CalmDarkMatte else CalmWhite,
                            border = BorderStroke(1.dp, if (isAllSelected) CalmDarkMatte else CalmHairline),
                            shadowElevation = if (isAllSelected) 3.dp else 1.dp,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "All Specialties",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isAllSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isAllSelected) CalmWhite else CalmInkNavy,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }
                        }
                    }

                    items(specialties) { specialty ->
                        val isSelected = uiState.selectedSpecialty == specialty
                        Surface(
                            onClick = { viewModel.onSpecialtySelected(specialty) },
                            shape = CalmLightShapes.Pill,
                            color = if (isSelected) CalmDarkMatte else CalmWhite,
                            border = BorderStroke(1.dp, if (isSelected) CalmDarkMatte else CalmHairline),
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                                Image(
                                    painter = painterResource(id = specialty.getIconRes()),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = specialty.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) CalmWhite else CalmInkNavy,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Row: Format (Online/In-Person) & City Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Online filter pill
                    item {
                        val isOnline = uiState.selectedConsultationType == ConsultationType.ONLINE
                        Surface(
                            onClick = { viewModel.onConsultationTypeSelected(if (isOnline) null else ConsultationType.ONLINE) },
                            shape = CalmLightShapes.Pill,
                            color = if (isOnline) CalmSphereBlue else CalmWhite.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, if (isOnline) CalmSphereBlue else CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Videocam,
                                    contentDescription = null,
                                    tint = if (isOnline) CalmWhite else CalmSphereBlue,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Online Video",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isOnline) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isOnline) CalmWhite else CalmInkNavy,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // In-Person filter pill
                    item {
                        val isInPerson = uiState.selectedConsultationType == ConsultationType.IN_PERSON
                        Surface(
                            onClick = { viewModel.onConsultationTypeSelected(if (isInPerson) null else ConsultationType.IN_PERSON) },
                            shape = CalmLightShapes.Pill,
                            color = if (isInPerson) CalmEmerald else CalmWhite.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, if (isInPerson) CalmEmerald else CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Place,
                                    contentDescription = null,
                                    tint = if (isInPerson) CalmWhite else CalmEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "In-Person Clinic",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isInPerson) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isInPerson) CalmWhite else CalmInkNavy,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // Cities
                    items(cities) { city ->
                        val isCitySelected = uiState.selectedCity == city
                        Surface(
                            onClick = { viewModel.onCitySelected(city) },
                            shape = CalmLightShapes.Pill,
                            color = if (isCitySelected) CalmHoneyGoldSoft else CalmWhite.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, if (isCitySelected) CalmHoneyGold else CalmHairline),
                            shadowElevation = 1.dp,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                Text(
                                    text = city,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isCitySelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isCitySelected) CalmInkNavy else CalmSlate,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Results count and active filter summary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${uiState.practitioners.size} practitioners available",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = CalmSlate,
                            fontSize = 12.5.sp
                        )
                    )

                    if (uiState.selectedCity != null || uiState.selectedSpecialty != null) {
                        Text(
                            text = listOfNotNull(uiState.selectedSpecialty?.displayName, uiState.selectedCity).joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSphereBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (uiState.practitioners.isEmpty()) {
                    CalmEmptyState(
                        title = "No practitioners match your search",
                        message = "Try clearing filters to view all available verified professionals across Zambia.",
                        actionText = "Reset Filters",
                        onAction = { viewModel.clearFilters() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.practitioners) { practitioner ->
                            SpecialistCard(
                                practitioner = practitioner,
                                onClick = { onSelectProvider(practitioner.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Beautiful, high-contrast Specialist Card following the warm-aesthetic philosophy of HomeScreen.
 * Incorporates doctor portrait imagery, verified status badges, specialty tags, and booking affordances.
 */
@Composable
fun SpecialistCard(
    practitioner: Practitioner,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CalmLightShapes.Prominent,
        color = CalmWhite,
        border = BorderStroke(1.dp, CalmHairline),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("provider_card_${practitioner.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Doctor Image/Avatar, Name, Rating, Specialty
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Doctor Photo / Portrait Container
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CalmMistSurface)
                        .border(1.5.dp, CalmHairline, RoundedCornerShape(18.dp))
                ) {
                    if (practitioner.imageResId != null) {
                        Image(
                            painter = painterResource(id = practitioner.imageResId),
                            contentDescription = practitioner.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Initials Fallback
                        ProviderAvatar(
                            initials = practitioner.avatarInitials,
                            size = 76,
                            backgroundColor = CalmDiscoveryAura
                        )
                    }

                    // Floating Online Indicator Dot in bottom-right corner of image
                    if (practitioner.supportedConsultationTypes.contains(ConsultationType.ONLINE)) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(CalmEmerald)
                                .border(1.5.dp, CalmWhite, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Doctor Information Column
                Column(modifier = Modifier.weight(1f)) {
                    // Full Name + Rating Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = practitioner.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp
                            ),
                            color = CalmInkNavy,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Gold Star Rating Badge Pill
                        Surface(
                            shape = CalmLightShapes.Pill,
                            color = CalmHoneyGoldSoft,
                            border = BorderStroke(0.8.dp, CalmHoneyGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${practitioner.rating}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = CalmInkNavy,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Title & Primary Specialty
                    Text(
                        text = "${practitioner.title} • ${practitioner.primarySpecialty.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = CalmSphereBlue,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Verified Badge & Location Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        VerifiedBadge()
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = null,
                            tint = CalmSlate,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${practitioner.location.area}, ${practitioner.location.city}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio Excerpt with subtle typography
            Text(
                text = practitioner.bio,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmInkNavy.copy(alpha = 0.78f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            HorizontalDivider(color = CalmHairline)

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Row: Consultation Types & Price & Book affordance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Consultation Type Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    practitioner.supportedConsultationTypes.forEach { type ->
                        ConsultationTypeChip(type = type)
                    }
                }

                // Price & Arrow CTA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "K${practitioner.startingPriceZmw.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Normal,
                                color = CalmInkNavy,
                                fontSize = 17.sp
                            )
                        )
                    }

                    // Forward Arrow Circle in Matte Dark
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CalmDarkMatte)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View specialist",
                            tint = CalmWhite,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
