package com.example.features.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.HealthcareService
import com.example.core.model.Practitioner
import com.example.core.repository.PractitionerRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProviderDetailUiState(
    val practitioner: Practitioner? = null,
    val services: List<HealthcareService> = emptyList(),
    val isLoading: Boolean = true
)

class ProviderDetailViewModel(
    private val practitionerId: String,
    private val practitionerRepository: PractitionerRepository = AppRepositoryLocator.practitionerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProviderDetailUiState())
    val uiState: StateFlow<ProviderDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val provider = practitionerRepository.getPractitionerById(practitionerId)
            val servicesList = practitionerRepository.getServices(practitionerId)
            _uiState.value = ProviderDetailUiState(
                practitioner = provider,
                services = servicesList,
                isLoading = false
            )
        }
    }
}
