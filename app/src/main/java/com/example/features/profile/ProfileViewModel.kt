package com.example.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Patient
import com.example.core.model.PatientConsentRecord
import com.example.core.repository.PatientRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val patient: Patient? = null,
    val consents: List<PatientConsentRecord> = emptyList(),
    val isBiometricLockEnabled: Boolean = true,
    val isLowBandwidthMode: Boolean = false,
    val selectedLanguage: String = "English",
    val isEditProfileOpen: Boolean = false,
    val feedbackMessage: String? = null
)

class ProfileViewModel(
    private val patientRepository: PatientRepository = AppRepositoryLocator.patientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            combine(
                patientRepository.getCurrentPatient(),
                patientRepository.getConsentRecords()
            ) { patient, consents ->
                _uiState.value.copy(
                    patient = patient,
                    consents = consents
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openEditProfile() {
        _uiState.update { it.copy(isEditProfileOpen = true) }
    }

    fun closeEditProfile() {
        _uiState.update { it.copy(isEditProfileOpen = false) }
    }

    fun saveProfile(
        name: String,
        phone: String,
        city: String,
        email: String? = null,
        emergencyName: String? = null,
        emergencyPhone: String? = null,
        avatarUri: String? = null,
        avatarPresetId: String? = null
    ) {
        viewModelScope.launch {
            patientRepository.updatePatientProfile(
                name = name,
                phone = phone,
                city = city,
                email = email,
                emergencyName = emergencyName,
                emergencyPhone = emergencyPhone,
                avatarUri = avatarUri,
                avatarPresetId = avatarPresetId
            )
            _uiState.update {
                it.copy(
                    isEditProfileOpen = false,
                    feedbackMessage = "Profile updated successfully."
                )
            }
        }
    }

    fun updateAvatar(avatarUri: String?, presetId: String?) {
        val current = _uiState.value.patient ?: return
        viewModelScope.launch {
            patientRepository.updatePatientProfile(
                name = current.fullName,
                phone = current.phoneNumber,
                city = current.selectedCity,
                email = current.email,
                emergencyName = current.emergencyContactName,
                emergencyPhone = current.emergencyContactPhone,
                avatarUri = avatarUri,
                avatarPresetId = presetId
            )
            _uiState.update { it.copy(feedbackMessage = "Profile photo updated.") }
        }
    }

    fun toggleConsent(consentType: String, isGranted: Boolean) {
        viewModelScope.launch {
            patientRepository.toggleConsent(consentType, isGranted)
            _uiState.update { it.copy(feedbackMessage = "Consent setting updated.") }
        }
    }

    fun toggleBiometricLock() {
        _uiState.update { it.copy(isBiometricLockEnabled = !it.isBiometricLockEnabled) }
    }

    fun toggleLowBandwidthMode() {
        _uiState.update { it.copy(isLowBandwidthMode = !it.isLowBandwidthMode) }
    }

    fun updateLanguage(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun updateCity(city: String) {
        val current = _uiState.value.patient ?: return
        viewModelScope.launch {
            patientRepository.updatePatientProfile(
                name = current.fullName,
                phone = current.phoneNumber,
                city = city,
                email = current.email,
                emergencyName = current.emergencyContactName,
                emergencyPhone = current.emergencyContactPhone,
                avatarUri = current.avatarUri,
                avatarPresetId = current.avatarPresetId
            )
            _uiState.update { it.copy(feedbackMessage = "Location updated to $city.") }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
