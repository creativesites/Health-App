package com.example.features.care

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.core.design.*
import com.example.ui.theme.*

@Composable
fun CareScreen(
    viewModel: CareViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedMoodScore by remember { mutableIntStateOf(4) }
    var selectedFeelings by remember { mutableStateOf(setOf("Grounded", "Grateful")) }
    var moodNote by remember { mutableStateOf("") }

    var journalTitle by remember { mutableStateOf("") }
    var journalReflection by remember { mutableStateOf("") }

    // Breathing rhythm state
    var isBreathingActive by remember { mutableStateOf(true) }
    var selectedBreathingMode by remember { mutableStateOf("Calming (4-4-6)") }

    // Add Goal Dialog
    if (uiState.isAddGoalDialogOpen) {
        var goalTitle by remember { mutableStateOf("") }
        var goalCategory by remember { mutableStateOf("Mindfulness") }
        var goalDesc by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = { viewModel.closeAddGoalDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CalmWhite,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "New Care Plan Goal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Define a supportive personal wellbeing habit",
                        style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title") },
                        placeholder = { Text("e.g. Evening herbal tea & wind-down") },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = goalDesc,
                        onValueChange = { goalDesc = it },
                        label = { Text("Target Description") },
                        placeholder = { Text("e.g. 15 minutes without phone before bed") },
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Mindfulness", "Physical", "Rest", "Hydration").forEach { cat ->
                            val isSel = goalCategory == cat
                            FilterChip(
                                selected = isSel,
                                onClick = { goalCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CalmDiscoveryAura,
                                    selectedLabelColor = CalmSphereBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.closeAddGoalDialog() },
                            shape = CalmLightShapes.Pill,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (goalTitle.isNotBlank()) {
                                    viewModel.saveNewGoal(
                                        title = goalTitle.trim(),
                                        category = goalCategory,
                                        targetDescription = goalDesc.trim().ifBlank { "Daily wellness habit" }
                                    )
                                }
                            },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(containerColor = CalmSphereBlue),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Add Goal")
                        }
                    }
                }
            }
        }
    }

    // Mood Check-in Dialog
    if (uiState.isMoodCheckInDialogOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeMoodDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CalmWhite,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Daily Reflective Check-In",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Take a pause to recognize your mind and physical state.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate
                        )
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    val moods = listOf(
                        Pair(1, "Restless"),
                        Pair(2, "Tender"),
                        Pair(3, "Seeking Clarity"),
                        Pair(4, "Steady"),
                        Pair(5, "Grounded & Thriving")
                    )

                    // Mood Scale Buttons
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        moods.forEach { (score, label) ->
                            val isSelected = selectedMoodScore == score
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) CalmSphereBlue else CalmMistSurface,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) CalmSphereBlue else CalmHairline
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { selectedMoodScore = score }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$score",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = OutfitFontFamily,
                                        fontSize = 18.sp,
                                        color = if (isSelected) CalmWhite else CalmInkNavy
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmDiscoveryAura,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = moods.first { it.first == selectedMoodScore }.second,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmSphereBlue,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feeling tags
                    Text(
                        text = "Primary Feelings:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = CalmInkNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val allFeelings = listOf("Grateful", "Calm", "Reflective", "Overwhelmed", "Hopeful", "Fatigued", "Focused")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allFeelings.take(4).forEach { tag ->
                            val isSelected = selectedFeelings.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedFeelings = if (isSelected) selectedFeelings - tag else selectedFeelings + tag
                                },
                                label = { Text(tag, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CalmDiscoveryAura,
                                    selectedLabelColor = CalmSphereBlue
                                )
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allFeelings.drop(4).forEach { tag ->
                            val isSelected = selectedFeelings.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedFeelings = if (isSelected) selectedFeelings - tag else selectedFeelings + tag
                                },
                                label = { Text(tag, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CalmDiscoveryAura,
                                    selectedLabelColor = CalmSphereBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = moodNote,
                        onValueChange = { moodNote = it },
                        placeholder = { Text("Optional note or reflection...", color = CalmSoftSlate, fontSize = 13.sp) },
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.closeMoodDialog() },
                            shape = CalmLightShapes.Pill,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val label = moods.first { it.first == selectedMoodScore }.second
                                viewModel.saveMood(
                                    moodValue = selectedMoodScore,
                                    moodLabel = label,
                                    feelings = selectedFeelings.toList().ifEmpty { listOf("Reflective") },
                                    note = moodNote.ifBlank { null }
                                )
                                moodNote = ""
                            },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(containerColor = CalmSphereBlue),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("save_mood_dialog_btn")
                        ) {
                            Text("Save Check-in")
                        }
                    }
                }
            }
        }
    }

    // Journal Entry Dialog
    if (uiState.isJournalDialogOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeJournalDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CalmWhite,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Private Wellness Journal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        ),
                        color = CalmInkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Confidential thoughts, saved locally on your device.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CalmSlate)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = journalTitle,
                        onValueChange = { journalTitle = it },
                        label = { Text("Reflection Title") },
                        placeholder = { Text("e.g. Afternoon Clarity & Relief") },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = journalReflection,
                        onValueChange = { journalReflection = it },
                        label = { Text("Your Thoughts") },
                        placeholder = { Text("What brought you comfort or peace today? What are you holding onto?") },
                        minLines = 4,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.closeJournalDialog() },
                            shape = CalmLightShapes.Pill,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (journalTitle.isNotBlank()) {
                                    viewModel.saveJournalEntry(
                                        title = journalTitle.trim(),
                                        reflection = journalReflection.trim(),
                                        moodScore = 4
                                    )
                                    journalTitle = ""
                                    journalReflection = ""
                                }
                            },
                            shape = CalmLightShapes.Pill,
                            colors = ButtonDefaults.buttonColors(containerColor = CalmSphereBlue),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("save_journal_entry_btn")
                        ) {
                            Text("Save Reflection")
                        }
                    }
                }
            }
        }
    }

    AuraBackground(
        aura = AuraType.SelfCare,
        secondaryAura = AuraType.Discovery,
        intensity = 1.15f
    ) {
        // Editorial background image with subtle translucency
        Image(
            painter = painterResource(id = R.drawable.bg_editorial_care_loop),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.08f,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                CalmTopBar(
                    title = "Wellbeing & Care",
                    subtitle = "Breathwork rhythms, mindful check-ins & habits"
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Streak Banner
                Surface(
                    shape = CalmLightShapes.Standard,
                    color = CalmHoneyGoldLight.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, CalmHoneyGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CalmHoneyGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = CalmHoneyGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "5-Day Mindfulness Streak",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "You have checked in for 5 consecutive days. Keep nurturing your space.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Care Continuity Sanctuary Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CalmWhite,
                    border = BorderStroke(1.dp, CalmHairline),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ill_care_continuity),
                        contentDescription = "Care Continuity & Grounding",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grounding Rhythm Breathwork Card
                CalmCard(
                    shape = CalmLightShapes.Prominent,
                    backgroundColor = CalmWhite,
                    modifier = Modifier.testTag("breathwork_card")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Grounding Rhythm",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    ),
                                    color = CalmInkNavy
                                )
                                Text(
                                    text = "Follow the soothing pulse to anchor your nervous system",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate
                                    )
                                )
                            }

                            // Play / Pause breathing button
                            IconButton(
                                onClick = { isBreathingActive = !isBreathingActive },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CalmDiscoveryAura)
                            ) {
                                Icon(
                                    imageVector = if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle breathing",
                                    tint = CalmSphereBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Animated Sphere Presentation
                        TheSphere(
                            size = 110.dp,
                            isBreathing = isBreathingActive
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mode Selector Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Calming (4-4-6)", "Box (4-4-4)", "Deep Sleep (4-7-8)").forEach { mode ->
                                val isSelected = selectedBreathingMode == mode
                                Surface(
                                    shape = CalmLightShapes.Pill,
                                    color = if (isSelected) CalmDiscoveryAura else CalmMistSurface,
                                    border = BorderStroke(1.dp, if (isSelected) CalmSphereBlue else CalmHairline),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedBreathingMode = mode }
                                ) {
                                    Text(
                                        text = mode,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) CalmSphereBlue else CalmSlate,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBreathingActive) "Inhale gently as sphere expands • Exhale softly as it rests" else "Tap play to begin grounding rhythm",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSphereBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Daily Mood Check-in Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Daily Mood Check-ins",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = CalmInkNavy
                        )
                        Text(
                            text = "Persisted securely in local database",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = { viewModel.openMoodDialog() },
                        shape = CalmLightShapes.Pill,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CalmSphereBlue,
                            contentColor = CalmWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_log_mood")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log Mood",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mood Check-in Cards
                if (uiState.moodCheckIns.isEmpty()) {
                    CalmCard(
                        shape = CalmLightShapes.Standard,
                        backgroundColor = CalmWhite
                    ) {
                        Text(
                            text = "No check-ins recorded yet today. Tap 'Log Mood' above to save your first reflection.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate
                            )
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.moodCheckIns) { checkIn ->
                            Surface(
                                shape = CalmLightShapes.Standard,
                                color = CalmWhite,
                                border = BorderStroke(1.dp, CalmHairline),
                                shadowElevation = 1.dp,
                                modifier = Modifier.width(180.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            shape = CalmLightShapes.Pill,
                                            color = when (checkIn.moodValue) {
                                                5, 4 -> CalmEmerald.copy(alpha = 0.15f)
                                                3 -> CalmHoneyGoldLight
                                                else -> CalmCrisisCoralSoft
                                            }
                                        ) {
                                            Text(
                                                text = "${checkIn.moodValue}/5",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = OutfitFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (checkIn.moodValue) {
                                                        5, 4 -> CalmEmerald
                                                        3 -> CalmHoneyGoldDark
                                                        else -> CalmCrisisCoral
                                                    }
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        TheSphere(size = 14.dp)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = checkIn.moodLabel,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        ),
                                        color = CalmInkNavy
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = checkIn.dateLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSoftSlate,
                                            fontSize = 10.sp
                                        )
                                    )
                                    if (!checkIn.note.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"${checkIn.note}\"",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = InterFontFamily,
                                                color = CalmSlate,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Active Care Plan Goals Section
                val completedCount = uiState.careGoals.count { it.isCompleted }
                val totalCount = uiState.careGoals.size
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_util_care_plan),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Care Plan Goals",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = "$completedCount of $totalCount completed today",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmEmerald,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    TextButton(
                        onClick = { viewModel.openAddGoalDialog() },
                        modifier = Modifier.testTag("btn_add_care_goal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = CalmSphereBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Goal",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSphereBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                uiState.careGoals.forEach { goal ->
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
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = goal.isCompleted,
                                    onCheckedChange = { viewModel.toggleGoal(goal.id, it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CalmEmerald,
                                        uncheckedColor = CalmSlate
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else null
                                        ),
                                        color = if (goal.isCompleted) CalmSlate else CalmInkNavy
                                    )
                                    Text(
                                        text = goal.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSphereBlue,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = goal.targetDescription,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = InterFontFamily,
                                            color = CalmSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = CalmLightShapes.Pill,
                                color = if (goal.isCompleted) CalmEmerald.copy(alpha = 0.15f) else CalmMistSurface
                            ) {
                                Text(
                                    text = if (goal.isCompleted) "Done" else "${goal.progressPercent}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (goal.isCompleted) CalmEmerald else CalmSlate
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { if (goal.isCompleted) 1f else goal.progressPercent / 100f },
                            color = CalmEmerald,
                            trackColor = CalmMistSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CalmLightShapes.Pill)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Private Journal Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Private Wellness Journal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = CalmInkNavy
                        )
                        Text(
                            text = "Personal reflections & therapeutic insights",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 11.sp
                            )
                        )
                    }

                    TextButton(
                        onClick = { viewModel.openJournalDialog() },
                        modifier = Modifier.testTag("btn_add_journal_entry")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EditNote,
                            contentDescription = null,
                            tint = CalmSphereBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Write Entry",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSphereBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                uiState.journalEntries.forEach { entry ->
                    CalmCard(
                        shape = CalmLightShapes.Standard,
                        backgroundColor = CalmWhite,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = CalmInkNavy
                            )
                            Text(
                                text = entry.dateLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSoftSlate,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = entry.reflection,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Zambia Mental Health Support Card
                CalmCard(
                    shape = CalmLightShapes.Standard,
                    backgroundColor = CalmCrisisCoralSoft,
                    border = BorderStroke(1.dp, CalmCrisisCoral.copy(alpha = 0.25f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhoneInTalk,
                            contentDescription = null,
                            tint = CalmCrisisCoral,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lifeline / Childline Zambia (Toll-Free 116)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Confidential emotional & mental health crisis counsel available 24/7",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
