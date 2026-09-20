package com.example.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.design.*
import com.example.core.model.Patient
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSafety: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onRestartOnboarding: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val patient = uiState.patient
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateAvatar(uri.toString(), null)
        }
    }

    // City Selection Dialog
    var showCityDialog by remember { mutableStateOf(false) }
    val zambianCities = listOf("Lusaka", "Kitwe", "Ndola", "Livingstone", "Kabwe", "Chipata")

    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            shape = CalmLightShapes.Prominent,
            containerColor = CalmWhite,
            title = {
                Text(
                    text = "Select Primary City",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = CalmInkNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    zambianCities.forEach { city ->
                        val isSelected = patient?.selectedCity == city
                        Surface(
                            shape = CalmLightShapes.Standard,
                            color = if (isSelected) CalmDiscoveryAura else CalmWhite,
                            border = BorderStroke(1.dp, if (isSelected) CalmSphereBlue else CalmHairline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCity(city)
                                    showCityDialog = false
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    text = city,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) CalmSphereBlue else CalmInkNavy
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CalmSphereBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close", color = CalmSlate)
                }
            }
        )
    }

    // Comprehensive Edit Profile Dialog
    if (uiState.isEditProfileOpen && patient != null) {
        EditProfileModal(
            currentPatient = patient,
            onDismiss = { viewModel.closeEditProfile() },
            onSave = { name, phone, city, email, emergencyName, emergencyPhone, avatarUri, avatarPresetId ->
                viewModel.saveProfile(
                    name = name,
                    phone = phone,
                    city = city,
                    email = email,
                    emergencyName = emergencyName,
                    emergencyPhone = emergencyPhone,
                    avatarUri = avatarUri,
                    avatarPresetId = avatarPresetId
                )
            }
        )
    }

    AuraBackground(
        aura = AuraType.Discovery,
        secondaryAura = AuraType.Payments,
        intensity = 1.1f
    ) {
        Scaffold(
            topBar = {
                CalmTopBar(
                    title = "Patient Profile",
                    subtitle = "Personal identity, care records, and preferences",
                    actions = {
                        IconButton(
                            onClick = onNavigateToSafety,
                            modifier = Modifier.testTag("profile_safety_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = "Emergency Crisis Help",
                                tint = CalmCrisisCoral,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier.testTag("profile_notifs_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = CalmInkNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Hero Patient Profile Card
                CalmCard(
                    shape = CalmLightShapes.Hero,
                    backgroundColor = CalmWhite,
                    modifier = Modifier.testTag("patient_profile_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar with interactive edit badge
                            Box(
                                modifier = Modifier.testTag("avatar_container")
                            ) {
                                PatientAvatar(
                                    patient = patient,
                                    size = 80,
                                    onClick = { viewModel.openEditProfile() }
                                )
                                // Camera / Edit overlay badge
                                Surface(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = CircleShape,
                                    color = CalmSphereBlue,
                                    border = BorderStroke(2.dp, CalmWhite),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .testTag("avatar_edit_btn")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Change profile photo",
                                            tint = CalmWhite,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = patient?.fullName ?: "Kondwani Tembo",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 22.sp,
                                        letterSpacing = (-0.4).sp
                                    ),
                                    color = CalmInkNavy,
                                    modifier = Modifier.testTag("patient_name_text")
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = patient?.phoneNumber ?: "+260 97 5543210",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = patient?.email ?: "kondwani.tembo@example.zm",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = CalmHairline)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Location Chip + Edit Profile CTA
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CalmLightShapes.Pill,
                                color = CalmMistSurface,
                                border = BorderStroke(1.dp, CalmHairline),
                                modifier = Modifier
                                    .clickable { showCityDialog = true }
                                    .testTag("patient_city_pill")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Place,
                                        contentDescription = null,
                                        tint = CalmSphereBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${patient?.selectedCity ?: "Lusaka"}, Zambia",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            color = CalmSphereBlue,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            // Prominent Edit Profile Button
                            Surface(
                                onClick = { viewModel.openEditProfile() },
                                shape = CalmLightShapes.Pill,
                                color = CalmDiscoveryAura,
                                border = BorderStroke(1.dp, CalmSphereBlue.copy(alpha = 0.4f)),
                                modifier = Modifier.testTag("edit_profile_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        tint = CalmSphereBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Edit Profile",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalmSphereBlue,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Health Summary Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileMetricCard(
                        title = "Consultations",
                        value = "4 Past",
                        subtitle = "All completed",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileMetricCard(
                        title = "Streak",
                        value = "5 Days",
                        subtitle = "Mindful logs",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileMetricCard(
                        title = "Care Goals",
                        value = "3 Active",
                        subtitle = "Daily targets",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Healthcare Consent Architecture (HPCZ Compliant)
                Text(
                    text = "Consent & Privacy Management",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Granular privacy records compliant with Zambian data protection regulations.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = InterFontFamily,
                        color = CalmSlate
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                CalmCard(
                    shape = CalmLightShapes.Standard,
                    backgroundColor = CalmWhite
                ) {
                    uiState.consents.forEachIndexed { index, consent ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = consent.consentType,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = CalmInkNavy
                                    )
                                )
                                Text(
                                    text = consent.purpose,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = InterFontFamily,
                                        color = CalmSlate,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = consent.isGranted,
                                onCheckedChange = { checked ->
                                    viewModel.toggleConsent(consent.consentType, checked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CalmWhite,
                                    checkedTrackColor = CalmSphereBlue,
                                    uncheckedThumbColor = CalmSlate,
                                    uncheckedTrackColor = CalmMistSurface
                                )
                            )
                        }

                        if (index < uiState.consents.size - 1) {
                            HorizontalDivider(color = CalmHairline, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Preferences
                Text(
                    text = "Application Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = CalmInkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                CalmCard(
                    shape = CalmLightShapes.Standard,
                    backgroundColor = CalmWhite
                ) {
                    // Biometric Lock
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Fingerprint,
                            contentDescription = null,
                            tint = CalmSphereBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Biometric App Lock",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Prompt fingerprint or face unlock on launch",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Switch(
                            checked = uiState.isBiometricLockEnabled,
                            onCheckedChange = { viewModel.toggleBiometricLock() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CalmWhite,
                                checkedTrackColor = CalmSphereBlue
                            )
                        )
                    }

                    HorizontalDivider(color = CalmHairline, modifier = Modifier.padding(vertical = 12.dp))

                    // Low-Bandwidth Mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SignalCellularAlt,
                            contentDescription = null,
                            tint = CalmHoneyGoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Low-Bandwidth Mode",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Optimizes for 3G and rural network connectivity",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Switch(
                            checked = uiState.isLowBandwidthMode,
                            onCheckedChange = { viewModel.toggleLowBandwidthMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CalmWhite,
                                checkedTrackColor = CalmHoneyGoldDark
                            )
                        )
                    }

                    HorizontalDivider(color = CalmHairline, modifier = Modifier.padding(vertical = 12.dp))

                    // Language Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            tint = CalmInkNavy,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Language",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Current: ${uiState.selectedLanguage} (English, Bemba, Nyanja)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Emergency & Crisis Assistance
                CalmCard(
                    shape = CalmLightShapes.Standard,
                    backgroundColor = CalmCrisisCoralSoft,
                    border = BorderStroke(1.dp, CalmCrisisCoral.copy(alpha = 0.3f)),
                    onClick = onNavigateToSafety
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = CalmCrisisCoral,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Zambia Emergency & Crisis Support",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CalmInkNavy
                                )
                            )
                            Text(
                                text = "Lifeline 116 • Police/Medical 992 • Free & Confidential 24/7",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = InterFontFamily,
                                    color = CalmSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CalmCrisisCoral,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Switch Role / Log Out / Reset Demo
                OutlinedButton(
                    onClick = onRestartOnboarding,
                    shape = CalmLightShapes.Pill,
                    border = BorderStroke(1.dp, CalmHairline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CalmInkNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_sign_out_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Switch Role / Log Out to Specialist Mode",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileMetricCard(
    title: String,
    value: String,
    subtitle: String,
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
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSlate,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = CalmInkNavy,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = InterFontFamily,
                    color = CalmSlate.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun EditProfileModal(
    currentPatient: Patient,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        phone: String,
        city: String,
        email: String?,
        emergencyName: String?,
        emergencyPhone: String?,
        avatarUri: String?,
        avatarPresetId: String?
    ) -> Unit
) {
    var fullName by remember { mutableStateOf(currentPatient.fullName) }
    var phoneNumber by remember { mutableStateOf(currentPatient.phoneNumber) }
    var email by remember { mutableStateOf(currentPatient.email) }
    var selectedCity by remember { mutableStateOf(currentPatient.selectedCity) }
    var emergencyContactName by remember { mutableStateOf(currentPatient.emergencyContactName ?: "") }
    var emergencyContactPhone by remember { mutableStateOf(currentPatient.emergencyContactPhone ?: "") }
    var avatarUri by remember { mutableStateOf(currentPatient.avatarUri) }
    var selectedPreset by remember { mutableStateOf(currentPatient.avatarPresetId ?: "preset_amber") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri.toString()
            selectedPreset = ""
        }
    }

    val zambianCities = listOf("Lusaka", "Kitwe", "Ndola", "Livingstone", "Kabwe", "Chipata")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = CalmWhite,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(top = 36.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Modal Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            ),
                            color = CalmInkNavy
                        )
                        Text(
                            text = "Update your name, contact, and profile avatar",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = InterFontFamily,
                                color = CalmSlate
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_edit_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CalmSlate
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar Selection Section
                    Text(
                        text = "Profile Avatar & Photo",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Current preview avatar
                        val tempPatient = remember(fullName, avatarUri, selectedPreset) {
                            currentPatient.copy(
                                fullName = fullName.ifBlank { "User" },
                                avatarUri = avatarUri,
                                avatarPresetId = selectedPreset.ifBlank { null }
                            )
                        }
                        PatientAvatar(
                            patient = tempPatient,
                            size = 72
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = CalmLightShapes.Pill,
                                border = BorderStroke(1.dp, CalmSphereBlue),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CalmSphereBlue),
                                modifier = Modifier.testTag("pick_photo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Choose Photo",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            if (!avatarUri.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(
                                    onClick = {
                                        avatarUri = null
                                        selectedPreset = "preset_amber"
                                    }
                                ) {
                                    Text("Remove custom photo", color = CalmCrisisCoral, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Preset Avatar Styles
                    Text(
                        text = "Or choose a styled palette:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = InterFontFamily,
                            color = CalmSlate
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val presets = listOf(
                            Triple("preset_amber", "Warm Gold", CalmHoneyGoldLight),
                            Triple("preset_blue", "Sphere Blue", CalmDiscoveryAura),
                            Triple("preset_emerald", "Botanical", CalmEmerald.copy(alpha = 0.15f)),
                            Triple("preset_lavender", "Twilight", CalmSphereBlue.copy(alpha = 0.12f))
                        )

                        presets.forEach { (presetId, label, bg) ->
                            val isSelected = selectedPreset == presetId && avatarUri.isNullOrBlank()
                            Surface(
                                shape = CalmLightShapes.Standard,
                                color = bg,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) CalmSphereBlue else CalmHairline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        avatarUri = null
                                        selectedPreset = presetId
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (presetId) {
                                                    "preset_amber" -> CalmHoneyGold
                                                    "preset_blue" -> CalmSphereBlue
                                                    "preset_emerald" -> CalmEmerald
                                                    else -> CalmSlate
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = InterFontFamily,
                                            fontSize = 10.sp,
                                            color = CalmInkNavy,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = CalmHairline)

                    // Full Name Input
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Kondwani Tembo") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = CalmSphereBlue)
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_full_name")
                    )

                    // Phone Number Input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+260 97 1234567") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = CalmSphereBlue)
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_phone_number")
                    )

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("patient@example.zm") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = CalmSphereBlue)
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email")
                    )

                    // City Selection Chips
                    Text(
                        text = "Primary Zambian City",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        zambianCities.take(3).forEach { city ->
                            val isSel = selectedCity == city
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedCity = city },
                                label = { Text(city, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CalmDiscoveryAura,
                                    selectedLabelColor = CalmSphereBlue
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        zambianCities.drop(3).forEach { city ->
                            val isSel = selectedCity == city
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedCity = city },
                                label = { Text(city, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CalmDiscoveryAura,
                                    selectedLabelColor = CalmSphereBlue
                                )
                            )
                        }
                    }

                    // Emergency Contact
                    Text(
                        text = "Emergency Contact (Optional)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = CalmInkNavy
                        )
                    )

                    OutlinedTextField(
                        value = emergencyContactName,
                        onValueChange = { emergencyContactName = it },
                        label = { Text("Contact Name & Relationship") },
                        placeholder = { Text("e.g. Chileshe Tembo (Sister)") },
                        leadingIcon = {
                            Icon(Icons.Outlined.ContactPhone, contentDescription = null, tint = CalmSphereBlue)
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = emergencyContactPhone,
                        onValueChange = { emergencyContactPhone = it },
                        label = { Text("Contact Phone") },
                        placeholder = { Text("+260 96 1122334") },
                        leadingIcon = {
                            Icon(Icons.Outlined.PhoneInTalk, contentDescription = null, tint = CalmSphereBlue)
                        },
                        singleLine = true,
                        shape = CalmLightShapes.Standard,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CalmSphereBlue,
                            unfocusedBorderColor = CalmHairline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Modal CTA Footer
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = CalmLightShapes.Pill,
                        border = BorderStroke(1.dp, CalmHairline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CalmSlate),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (fullName.isNotBlank()) {
                                onSave(
                                    fullName.trim(),
                                    phoneNumber.trim(),
                                    selectedCity,
                                    email.trim().ifBlank { null },
                                    emergencyContactName.trim().ifBlank { null },
                                    emergencyContactPhone.trim().ifBlank { null },
                                    avatarUri,
                                    selectedPreset.ifBlank { null }
                                )
                            }
                        },
                        shape = CalmLightShapes.Pill,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CalmSphereBlue,
                            contentColor = CalmWhite
                        ),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("save_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Profile",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
