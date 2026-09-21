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

import java.time.YearMonth
import java.time.LocalDate

data class BookingUiState(
    val practitioner: Practitioner? = null,
    val allPractitioners: List<Practitioner> = emptyList(),
    val services: List<HealthcareService> = emptyList(),
    val selectedService: HealthcareService? = null,
    val selectedConsultationType: ConsultationType = ConsultationType.ONLINE,
    val visibleMonth: YearMonth = YearMonth.now(),
    val selectedDateIso: String = LocalDate.now().toString(),
    val availableDates: Set<String> = emptySet(), // Set of "YYYY-MM-DD"
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val selectedTimeSlot: TimeSlot? = null,
    val intakeNotes: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val createdAppointment: Appointment? = null,
    val conflictState: Boolean = false,
    val errorMessage: String? = null
)

class BookingViewModel(
    private var currentPractitionerId: String,
    private val practitionerRepository: PractitionerRepository = AppRepositoryLocator.practitionerRepository,
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository,
    private val availabilityRepository: com.example.core.repository.AvailabilityRepository = AppRepositoryLocator.availabilityRepository
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
            _uiState.update { it.copy(isLoading = true) }
            val provider = practitionerRepository.getPractitionerById(pId)
            val servicesList = practitionerRepository.getServices(pId)
            val initialService = servicesList.firstOrNull()
            
            _uiState.update {
                it.copy(
                    practitioner = provider,
                    services = servicesList,
                    selectedService = initialService
                )
            }
            
            // Re-calculate the month's available dates whenever practitioner changes
            calculateAvailableDates(pId, _uiState.value.visibleMonth)
        }
    }

    private suspend fun calculateAvailableDates(pId: String, month: YearMonth) {
        val rules = availabilityRepository.getAvailabilityRules(pId).first()
        val exceptions = availabilityRepository.getAvailabilityExceptions(pId).first()
        val appointments = appointmentRepository.getAppointments().first()

        val validDates = mutableSetOf<String>()
        val today = LocalDate.now()
        val serviceDuration = _uiState.value.selectedService?.durationMinutes ?: 30

        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (date.isBefore(today)) continue

            val slots = com.example.core.domain.SchedulingEngine.generateSlots(
                practitionerId = pId,
                targetDate = date,
                rules = rules,
                exceptions = exceptions,
                appointments = appointments,
                serviceDurationMinutes = serviceDuration
            )
            
            if (slots.isNotEmpty()) {
                validDates.add(date.toString())
            }
        }

        _uiState.update { state -> 
            val newSelectedDate = if (validDates.contains(state.selectedDateIso)) state.selectedDateIso else validDates.firstOrNull() ?: state.selectedDateIso
            state.copy(
                availableDates = validDates,
                isLoading = false,
                selectedDateIso = newSelectedDate
            )
        }
        
        // Load slots for the potentially newly selected date
        _uiState.value.selectedDateIso.let { iso ->
            val slotsForSelected = practitionerRepository.getAvailableTimeSlots(pId, iso)
            _uiState.update {
                it.copy(
                    availableTimeSlots = slotsForSelected,
                    selectedTimeSlot = slotsForSelected.firstOrNull { s -> s.isAvailable }
                )
            }
        }
    }

    fun onMonthChange(newMonth: YearMonth) {
        _uiState.update { it.copy(visibleMonth = newMonth, isLoading = true) }
        viewModelScope.launch {
            calculateAvailableDates(currentPractitionerId, newMonth)
        }
    }

    fun onSelectPractitioner(practitioner: Practitioner) {
        currentPractitionerId = practitioner.id
        loadPractitionerAndServices(practitioner.id)
    }

    fun onSelectService(service: HealthcareService) {
        _uiState.update { it.copy(selectedService = service, isLoading = true) }
        viewModelScope.launch {
            calculateAvailableDates(currentPractitionerId, _uiState.value.visibleMonth)
        }
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
            _uiState.update { it.copy(selectedTimeSlot = slot, conflictState = false) }
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
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null, conflictState = false) }
            
            // 12. BOOKING CONFLICT PROTECTION: Recalculate availability immediately before persisting
            val latestSlots = practitionerRepository.getAvailableTimeSlots(provider.id, state.selectedDateIso)
            val latestSlotInfo = latestSlots.find { it.id == slot.id }
            
            if (latestSlotInfo == null || !latestSlotInfo.isAvailable) {
                // Conflict detected!
                _uiState.update { 
                    it.copy(
                        isSubmitting = false, 
                        showConfirmationDialog = false, 
                        conflictState = true,
                        errorMessage = "The selected time slot is no longer available.",
                        availableTimeSlots = latestSlots,
                        selectedTimeSlot = null
                    ) 
                }
                return@launch
            }
            
            _uiState.update { it.copy(showConfirmationDialog = false) }

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

