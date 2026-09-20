package com.example.features.care

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.CareGoal
import com.example.core.model.JournalEntry
import com.example.core.model.MoodCheckIn
import com.example.core.repository.CareRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CareUiState(
    val careGoals: List<CareGoal> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val moodCheckIns: List<MoodCheckIn> = emptyList(),
    val isMoodCheckInDialogOpen: Boolean = false,
    val isJournalDialogOpen: Boolean = false,
    val isAddGoalDialogOpen: Boolean = false
)

class CareViewModel(
    private val careRepository: CareRepository = AppRepositoryLocator.careRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareUiState())
    val uiState: StateFlow<CareUiState> = _uiState.asStateFlow()

    init {
        loadCareData()
    }

    private fun loadCareData() {
        viewModelScope.launch {
            combine(
                careRepository.getCareGoals(),
                careRepository.getJournalEntries(),
                careRepository.getMoodCheckIns()
            ) { goals, journal, moods ->
                _uiState.value.copy(
                    careGoals = goals,
                    journalEntries = journal,
                    moodCheckIns = moods
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleGoal(goalId: String, completed: Boolean) {
        viewModelScope.launch {
            careRepository.toggleGoalProgress(goalId, completed)
        }
    }

    fun openAddGoalDialog() {
        _uiState.update { it.copy(isAddGoalDialogOpen = true) }
    }

    fun closeAddGoalDialog() {
        _uiState.update { it.copy(isAddGoalDialogOpen = false) }
    }

    fun saveNewGoal(title: String, category: String, targetDescription: String) {
        viewModelScope.launch {
            careRepository.addCareGoal(title, category, targetDescription)
            closeAddGoalDialog()
        }
    }

    fun openMoodDialog() {
        _uiState.update { it.copy(isMoodCheckInDialogOpen = true) }
    }

    fun closeMoodDialog() {
        _uiState.update { it.copy(isMoodCheckInDialogOpen = false) }
    }

    fun saveMood(moodValue: Int, moodLabel: String, feelings: List<String>, note: String?) {
        viewModelScope.launch {
            careRepository.recordMoodCheckIn(moodValue, moodLabel, feelings, note)
            closeMoodDialog()
        }
    }

    fun openJournalDialog() {
        _uiState.update { it.copy(isJournalDialogOpen = true) }
    }

    fun closeJournalDialog() {
        _uiState.update { it.copy(isJournalDialogOpen = false) }
    }

    fun saveJournalEntry(title: String, reflection: String, moodScore: Int) {
        viewModelScope.launch {
            careRepository.addJournalEntry(title, reflection, moodScore)
            closeJournalDialog()
        }
    }
}
