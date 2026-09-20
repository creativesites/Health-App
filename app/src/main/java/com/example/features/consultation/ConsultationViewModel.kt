package com.example.features.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Appointment
import com.example.core.repository.AppointmentRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ConnectionQuality(val label: String, val colorHex: Long) {
    EXCELLENT("Excellent connection", 0xFF059669),
    GOOD("Good connection", 0xFF10B981),
    WEAK("Weak connection — Audio prioritized", 0xFFF59E0B),
    RECONNECTING("Reconnecting...", 0xFFEF4444)
}

data class ConsultationUiState(
    val appointment: Appointment? = null,
    val isCameraOn: Boolean = true,
    val isMicOn: Boolean = true,
    val isSpeakerOn: Boolean = true,
    val isAudioOnlyFallback: Boolean = false,
    val connectionQuality: ConnectionQuality = ConnectionQuality.GOOD,
    val sessionDurationSeconds: Long = 184, // e.g. 03:04 elapsed
    val isInCall: Boolean = true,
    val showLeaveDialog: Boolean = false
)

class ConsultationViewModel(
    private val appointmentId: String,
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultationUiState())
    val uiState: StateFlow<ConsultationUiState> = _uiState.asStateFlow()

    init {
        loadAppointment()
        startCallTimer()
    }

    private fun loadAppointment() {
        viewModelScope.launch {
            val apt = appointmentRepository.getAppointmentById(appointmentId)
            _uiState.update { it.copy(appointment = apt) }
        }
    }

    private fun startCallTimer() {
        viewModelScope.launch {
            while (_uiState.value.isInCall) {
                delay(1000)
                _uiState.update { it.copy(sessionDurationSeconds = it.sessionDurationSeconds + 1) }
            }
        }
    }

    fun toggleCamera() {
        _uiState.update { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicOn = !it.isMicOn) }
    }

    fun toggleSpeaker() {
        _uiState.update { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleAudioOnlyFallback() {
        _uiState.update {
            val nextFallback = !it.isAudioOnlyFallback
            it.copy(
                isAudioOnlyFallback = nextFallback,
                connectionQuality = if (nextFallback) ConnectionQuality.WEAK else ConnectionQuality.GOOD,
                isCameraOn = if (nextFallback) false else it.isCameraOn
            )
        }
    }

    fun promptLeaveCall() {
        _uiState.update { it.copy(showLeaveDialog = true) }
    }

    fun dismissLeaveDialog() {
        _uiState.update { it.copy(showLeaveDialog = false) }
    }

    fun confirmLeaveCall(onLeave: () -> Unit) {
        _uiState.update { it.copy(isInCall = false, showLeaveDialog = false) }
        onLeave()
    }
}
