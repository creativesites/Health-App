package com.example.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.*
import com.example.core.repository.*
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val patient: Patient? = null,
    val upcomingAppointment: Appointment? = null,
    val unreadNotificationsCount: Int = 0,
    val activeCareGoals: List<CareGoal> = emptyList(),
    val latestMood: MoodCheckIn? = null,
    val isLowBandwidthMode: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val patientRepository: PatientRepository = AppRepositoryLocator.patientRepository,
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository,
    private val notificationRepository: NotificationRepository = AppRepositoryLocator.notificationRepository,
    private val careRepository: CareRepository = AppRepositoryLocator.careRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                patientRepository.getCurrentPatient(),
                appointmentRepository.getAppointments(),
                notificationRepository.getNotifications(),
                careRepository.getCareGoals(),
                careRepository.getMoodCheckIns()
            ) { patient, appointments, notifications, goals, moods ->
                val upcoming = appointments.firstOrNull {
                    it.status == AppointmentStatus.CONFIRMED ||
                    it.status == AppointmentStatus.PENDING_PAYMENT ||
                    it.status == AppointmentStatus.RESCHEDULED ||
                    it.status == AppointmentStatus.IN_PROGRESS
                }
                val unread = notifications.count { !it.isRead }
                HomeUiState(
                    patient = patient,
                    upcomingAppointment = upcoming,
                    unreadNotificationsCount = unread,
                    activeCareGoals = goals.take(2),
                    latestMood = moods.firstOrNull(),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleLowBandwidthMode() {
        _uiState.update { it.copy(isLowBandwidthMode = !it.isLowBandwidthMode) }
    }

    fun recordQuickMood(moodLabel: String) {
        viewModelScope.launch {
            val score = when (moodLabel) {
                "Grounded" -> 5
                "Steady" -> 4
                "Tender" -> 3
                "Seeking clarity" -> 3
                "Restless" -> 2
                else -> 4
            }
            careRepository.recordMoodCheckIn(
                moodValue = score,
                moodLabel = moodLabel,
                feelings = listOf(moodLabel),
                note = "Ambient check-in from Calm Light Home"
            )
        }
    }
}
