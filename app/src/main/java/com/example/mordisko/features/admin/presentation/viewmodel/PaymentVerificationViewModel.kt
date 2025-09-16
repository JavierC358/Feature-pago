package com.example.mordisko.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.cart.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentVerificationViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentVerificationUiState())
    val state: StateFlow<PaymentVerificationUiState> = _state

    fun submitPaymentVerification(
        orderNumber: String,
        montoBs: String,
        referenciaLast4: String,
        telefonoPagador: String,
        razonSocial: String?,
        rif: String?,
        direccion: String?
    ) {
        viewModelScope.launch {
            _state.value = PaymentVerificationUiState(isLoading = true)
            android.util.Log.d("VerifyVM", "submitPaymentVerification() order=$orderNumber")
            val result = orderRepository.submitPaymentVerification(
                orderNumber = orderNumber,
                amountBsInput = montoBs,
                referenceLast4 = referenciaLast4,
                payerPhone = telefonoPagador,
                razonSocial = razonSocial,
                rif = rif,
                direccion = direccion
            )
            android.util.Log.d("VerifyVM", "repo result isSuccess=${result.isSuccess} err=${result.exceptionOrNull()?.message}")
            _state.value = if (result.isSuccess) {
                PaymentVerificationUiState(isSuccess = true)
            } else {
                PaymentVerificationUiState(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Error enviando verificación"
                )
            }
        }
    }

    fun resetState() { _state.value = PaymentVerificationUiState() }
}

data class PaymentVerificationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)