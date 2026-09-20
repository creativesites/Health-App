package com.example.features.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.*
import com.example.core.repository.AppointmentRepository
import com.example.core.repository.PractitionerRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingUiState(
    val practitioner: Practitioner? = null,
    val allPractitioners: List<Practitioner> = emptyList(),
    val services: List<HealthcareService> = emptyList(),
    val selectedService: HealthcareService? = null,
    val selectedConsultationType: ConsultationType = ConsultationType.ONLINE,
    val selectedDateIso: String = "2026-09-22",
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val selectedTimeSlot: TimeSlot? = null,
    val intakeNotes: String = "",
    val isSubmitting: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val createdAppointment: Appointment? = null,
    val errorMessage: String? = null
)

class BookingViewModel(
    private var currentPractitionerId: String,
    private val practitionerRepository: PractitionerRepository = AppRepositoryLocator.practitionerRepository,
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val practitioners = practitionerRepository.getPractitioners().first()
            _uiState.update { it.copy(allPractitioners = practitioners) }

            val targetId = if (currentPractitionerId.isBlank() || currentPractitionerId == "default") {
                practitioners.firstOrNull()?.id ?: ""
            } else {
                currentPractitionerId
            }
            currentPractitionerId = targetId
            loadPractitionerAndServices(targetId)
        }
    }

    private fun loadPractitionerAndServices(pId: String) {
        viewModelScope.launch {
            val provider = practitionerRepository.getPractitionerById(pId)
            val servicesList = practitionerRepository.getServices(pId)
            val initialService = servicesList.firstOrNull()
            val slots = practitionerRepository.getAvailableTimeSlots(pId, _uiState.value.selectedDateIso)

            _uiState.update {
                it.copy(
                    practitioner = provider,
                    services = servicesList,
                    selectedService = initialService,
                    availableTimeSlots = slots,
                    selectedTimeSlot = slots.firstOrNull { slot -> slot.isAvailable }
                )
            }
        }
    }

    fun onSelectPractitioner(practitioner: Practitioner) {
        currentPractitionerId = practitioner.id
        loadPractitionerAndServices(practitioner.id)
    }

    fun onSelectService(service: HealthcareService) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun onSelectConsultationType(type: ConsultationType) {
        _uiState.update { it.copy(selectedConsultationType = type) }
    }

    fun onSelectDate(dateIso: String) {
        _uiState.update { it.copy(selectedDateIso = dateIso) }
        viewModelScope.launch {
            val slots = practitionerRepository.getAvailableTimeSlots(currentPractitionerId, dateIso)
            _uiState.update {
                it.copy(
                    availableTimeSlots = slots,
                    selectedTimeSlot = slots.firstOrNull { s -> s.isAvailable }
                )
            }
        }
    }

    fun onSelectTimeSlot(slot: TimeSlot) {
        if (slot.isAvailable) {
            _uiState.update { it.copy(selectedTimeSlot = slot) }
        }
    }

    fun onIntakeNotesChange(notes: String) {
        _uiState.update { it.copy(intakeNotes = notes) }
    }

    fun onShowConfirmationDialog(show: Boolean) {
        _uiState.update { it.copy(showConfirmationDialog = show) }
    }

    fun confirmBooking(onSuccess: (appointmentId: String) -> Unit) {
        val state = _uiState.value
        val provider = state.practitioner
        val service = state.selectedService
        val slot = state.selectedTimeSlot

        if (provider == null || service == null || slot == null) {
            _uiState.update { it.copy(errorMessage = "Please choose a doctor, service, and time slot") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null, showConfirmationDialog = false) }
            val apt = appointmentRepository.bookAppointment(
                practitioner = provider,
                service = service,
                dateIso = state.selectedDateIso,
                timeSlotLabel = slot.timeLabel,
                consultationType = state.selectedConsultationType,
                intakeNotes = state.intakeNotes
            )
            _uiState.update { it.copy(isSubmitting = false, createdAppointment = apt) }
            onSuccess(apt.id)
        }
    }
}

