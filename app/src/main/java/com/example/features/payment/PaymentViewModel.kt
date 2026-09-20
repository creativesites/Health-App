package com.example.features.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.*
import com.example.core.repository.AppointmentRepository
import com.example.core.repository.PaymentRepository
import com.example.core.repository.mock.AppRepositoryLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentUiState(
    val appointment: Appointment? = null,
    val selectedProvider: PaymentProviderType = PaymentProviderType.MTN_MOMO,
    val mobileNumber: String = "+260 97 5543210",
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val completedTransaction: PaymentTransaction? = null,
    val errorMessage: String? = null,
    val isDemoSandboxMode: Boolean = true
)

class PaymentViewModel(
    private val appointmentId: String,
    private val appointmentRepository: AppointmentRepository = AppRepositoryLocator.appointmentRepository,
    private val paymentRepository: PaymentRepository = AppRepositoryLocator.paymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        loadAppointment()
    }

    private fun loadAppointment() {
        viewModelScope.launch {
            val apt = appointmentRepository.getAppointmentById(appointmentId)
            _uiState.update { it.copy(appointment = apt) }
        }
    }

    fun onSelectProvider(provider: PaymentProviderType) {
        _uiState.update {
            val defaultPrefix = when (provider) {
                PaymentProviderType.MTN_MOMO -> "+260 96 "
                PaymentProviderType.AIRTEL_MONEY -> "+260 97 "
                PaymentProviderType.ZAMTEL_KWACHA -> "+260 95 "
                else -> "+260 "
            }
            it.copy(
                selectedProvider = provider,
                mobileNumber = defaultPrefix + "5543210"
            )
        }
    }

    fun onMobileNumberChange(number: String) {
        _uiState.update { it.copy(mobileNumber = number) }
    }

    fun submitMobileMoneyPayment(onPaymentSuccess: () -> Unit) {
        val state = _uiState.value
        val apt = state.appointment ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(paymentStatus = PaymentStatus.PROCESSING, errorMessage = null) }

            // Realistic network latency simulation
            delay(1500)

            val txn = paymentRepository.initiateMobileMoneyPayment(
                appointmentId = apt.id,
                provider = state.selectedProvider,
                phoneNumber = state.mobileNumber,
                amountZmw = apt.priceZmw
            )

            // Update appointment status to CONFIRMED
            appointmentRepository.updateAppointmentStatus(apt.id, AppointmentStatus.CONFIRMED)

            _uiState.update {
                it.copy(
                    paymentStatus = PaymentStatus.PAID,
                    completedTransaction = txn
                )
            }
            onPaymentSuccess()
        }
    }
}
