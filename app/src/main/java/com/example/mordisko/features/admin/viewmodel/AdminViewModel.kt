package com.example.mordisko.features.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(AdminVerificacionesState())
    val state = _state.asStateFlow()

    fun loadVerificaciones() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = mutableListOf<VerificacionPago>()

            val orders = firestore.collection("orders").get().await()
            for (order in orders.documents) {
                val orderId = order.id
                val pagoDoc = firestore.collection("orders")
                    .document(orderId)
                    .collection("payment_verification")
                    .document("info")
                    .get().await()

                if (pagoDoc.exists()) {
                    val data = pagoDoc.data
                    if (data != null) {
                        result.add(
                            VerificacionPago(
                                orderNumber = orderId,
                                amountPaid = data["amountPaid"]?.toString() ?: "",
                                referenceLast4 = data["referenceLast4"]?.toString() ?: "",
                                phoneNumber = data["phoneNumber"]?.toString() ?: "",
                                status = data["status"]?.toString() ?: "pendiente"
                            )
                        )
                    }
                }
            }

            _state.value = AdminVerificacionesState(verificaciones = result)
        }
    }

    fun marcarComoVerificada(orderNumber: String) {
        viewModelScope.launch {
            // 1. Actualiza la subcolección
            firestore.collection("orders")
                .document(orderNumber)
                .collection("payment_verification")
                .document("info")
                .update("status", "verificado")
                .await()

            // 2. Actualiza también el campo 'paymentStatus' en el documento de la orden
            firestore.collection("orders")
                .document(orderNumber)
                .update("paymentStatus", "verificado")
                .await()

            // 3. Refresca la lista en pantalla
            loadVerificaciones()
        }
    }
}

data class AdminVerificacionesState(
    val isLoading: Boolean = false,
    val verificaciones: List<VerificacionPago> = emptyList()
)

data class VerificacionPago(
    val orderNumber: String,
    val amountPaid: String,
    val referenceLast4: String,
    val phoneNumber: String,
    val status: String
)