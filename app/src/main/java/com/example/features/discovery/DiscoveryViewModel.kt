package com.example.features.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.*
import com.example.core.repository.PractitionerRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DiscoveryUiState(
    val searchQuery: String = "",
    val selectedSpecialty: SpecialtyCategory? = null,
    val selectedCity: String? = null,
    val selectedConsultationType: ConsultationType? = null,
    val practitioners: List<Practitioner> = emptyList(),
    val isLoading: Boolean = false
)

class DiscoveryViewModel(
    private val practitionerRepository: PractitionerRepository = AppRepositoryLocator.practitionerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {
        loadPractitioners()
    }

    fun setInitialSpecialty(specialty: SpecialtyCategory?) {
        if (specialty != null) {
            _uiState.update { it.copy(selectedSpecialty = specialty) }
            loadPractitioners()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadPractitioners()
    }

    fun onSpecialtySelected(specialty: SpecialtyCategory?) {
        _uiState.update { it.copy(selectedSpecialty = if (it.selectedSpecialty == specialty) null else specialty) }
        loadPractitioners()
    }

    fun onCitySelected(city: String?) {
        _uiState.update { it.copy(selectedCity = if (it.selectedCity == city) null else city) }
        loadPractitioners()
    }

    fun onConsultationTypeSelected(type: ConsultationType?) {
        _uiState.update { it.copy(selectedConsultationType = if (it.selectedConsultationType == type) null else type) }
        loadPractitioners()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedSpecialty = null,
                selectedCity = null,
                selectedConsultationType = null
            )
        }
        loadPractitioners()
    }

    private fun loadPractitioners() {
        val current = _uiState.value
        viewModelScope.launch {
            practitionerRepository.searchPractitioners(
                query = current.searchQuery,
                specialty = current.selectedSpecialty,
                city = current.selectedCity,
                consultationType = current.selectedConsultationType
            ).collect { results ->
                _uiState.update { it.copy(practitioners = results, isLoading = false) }
            }
        }
    }
}
