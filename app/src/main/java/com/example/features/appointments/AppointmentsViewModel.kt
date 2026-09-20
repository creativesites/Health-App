package com.example.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Appointment
import com.example.core.model.AppointmentStatus
import com.example.core.repository.AppointmentRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppointmentTab {
    UPCOMING,
    PAST,
    CANCELLED
}

data class AppointmentsUiState(
    val selectedTab: AppointmentTab = AppointmentTab.UPCOMING,
    val allAppointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val actionFeedbackMessage: String? = null
)

class AppointmentsViewModel(
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            appointmentRepository.getAppointments().collect { list ->
                _uiState.update { it.copy(allAppointments = list, isLoading = false) }
            }
        }
    }

    fun onTabSelected(tab: AppointmentTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            appointmentRepository.cancelAppointment(appointmentId, "User requested cancellation")
            _uiState.update { it.copy(actionFeedbackMessage = "Appointment cancelled successfully.") }
        }
    }

    fun rescheduleAppointment(appointmentId: String) {
        viewModelScope.launch {
            appointmentRepository.rescheduleAppointment(
                appointmentId = appointmentId,
                newDateIso = "Next Monday",
                newTimeSlot = "11:00 - 11:50"
            )
            _uiState.update { it.copy(actionFeedbackMessage = "Appointment rescheduled to Next Monday 11:00.") }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(actionFeedbackMessage = null) }
    }
}
