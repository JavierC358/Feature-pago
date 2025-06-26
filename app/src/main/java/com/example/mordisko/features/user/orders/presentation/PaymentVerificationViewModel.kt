package com.example.mordisko.features.user.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentVerificationState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentVerificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentVerificationState())
    val state = _state.asStateFlow()

    fun submitPaymentVerification(
        orderNumber: String,
        amountPaid: String,
        referenceLast4: String,
        phoneNumber: String
    ) {
        _state.value = PaymentVerificationState(isLoading = true)

        val paymentData = hashMapOf(
            "amountPaid" to amountPaid,
            "referenceLast4" to referenceLast4,
            "phoneNumber" to phoneNumber,
            "status" to "pendiente", // Estado inicial
            "timestamp" to System.currentTimeMillis()
        )

        viewModelScope.launch {
            firestore.collection("orders")
                .document(orderNumber)
                .collection("payment_verification")
                .document("info") // puedes usar también .add(paymentData) para ID automático
                .set(paymentData)
                .addOnSuccessListener {
                    _state.value = PaymentVerificationState(isSuccess = true)
                }
                .addOnFailureListener { e ->
                    _state.value = PaymentVerificationState(errorMessage = e.message)
                }
        }
    }

    fun resetState() {
        _state.value = PaymentVerificationState()
    }
}