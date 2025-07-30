package com.example.mordisko.features.admin.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(AdminVerificacionesState())
    val state = _state.asStateFlow()

    private val _exchangeRate = MutableStateFlow(0.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    init {
        Log.d("TEST_INIT", "Entrando al init del AdminViewModel")
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                val uid = auth.currentUser?.uid ?: return@launch

                Log.d("🔥FCM_TOKEN", "Token del admin: $token")

                firestore.collection("admin_tokens").document(uid).set(
                    mapOf("token" to token)
                ).await()

                Log.d("🔥FCM_TOKEN", "Token guardado exitosamente en admin_tokens.")
            } catch (e: Exception) {
                Log.e("🔥FCM_TOKEN", "Error obteniendo o guardando el token FCM", e)
            }
        }
    }

    fun loadVerificaciones() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = mutableListOf<VerificacionPago>()

            try {
                val orders = firestore.collection("orders")
                    .get()
                    .await()

                for (order in orders.documents) {
                    val orderId = order.id

                    val pagoDoc = firestore.collection("orders")
                        .document(orderId)
                        .collection("payment_verification")
                        .document("info")
                        .get()
                        .await()

                    if (pagoDoc.exists()) {
                        // Caso Pago Móvil con subcolección
                        val data = pagoDoc.data
                        if (data != null) {
                            result.add(
                                VerificacionPago(
                                    orderNumber = orderId,
                                    amountPaid = data["amountPaid"]?.toString() ?: "",
                                    referenceLast4 = data["referenceLast4"]?.toString() ?: "",
                                    phoneNumber = data["phoneNumber"]?.toString() ?: "",
                                    status = data["status"]?.toString()
                                        ?: order.getString("paymentStatus")
                                        ?: "pendiente"
                                )
                            )
                        }
                    } else {
                        // Caso Efectivo o Punto de Venta (sin subcolección)
                        val status = order.getString("paymentStatus") ?: "pendiente"
                        result.add(
                            VerificacionPago(
                                orderNumber = orderId,
                                amountPaid = order.getDouble("totalBs")?.toString() ?: "",
                                referenceLast4 = "--",
                                phoneNumber = "--",
                                status = status
                            )
                        )
                    }
                }

                _state.value = AdminVerificacionesState(
                    verificaciones = result,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error cargando verificaciones", e)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun marcarComoVerificada(orderNumber: String) {
        viewModelScope.launch {
            try {
                // Actualiza la subcolección si existe
                val pagoDocRef = firestore.collection("orders")
                    .document(orderNumber)
                    .collection("payment_verification")
                    .document("info")

                val pagoDoc = pagoDocRef.get().await()
                if (pagoDoc.exists()) {
                    pagoDocRef.update("status", "verificado").await()
                }

                // Actualiza el campo paymentStatus en la orden
                firestore.collection("orders")
                    .document(orderNumber)
                    .update("paymentStatus", "verificado")
                    .await()

                // Recargar para reflejar cambios
                loadVerificaciones()
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error marcando como verificada", e)
            }
        }
    }

    fun loadExchangeRate() {
        viewModelScope.launch {
            val doc = firestore.collection("config").document("settings").get().await()
            val tasa = doc.getDouble("exchangeRateUsdToVes") ?: 0.0
            _exchangeRate.value = tasa
        }
    }

    fun guardarExchangeRate(nuevaTasa: Double) {
        viewModelScope.launch {
            firestore.collection("config").document("settings")
                .update("exchangeRateUsdToVes", nuevaTasa)
        }
    }

    fun guardarTokenFcm() {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val token = FirebaseMessaging.getInstance().token.await()

                val userDocRef = firestore.collection("users").document(uid)
                val snapshot = userDocRef.get().await()
                val existingTokens = snapshot.get("fcmTokens") as? List<String> ?: emptyList()

                if (!existingTokens.contains(token)) {
                    val updatedTokens = existingTokens.toMutableList().apply { add(token) }
                    userDocRef.update("fcmTokens", updatedTokens)
                    Log.d("FCM", "Token FCM agregado correctamente.")
                } else {
                    Log.d("FCM", "Token FCM ya existe, no se actualiza.")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error guardando token FCM: ${e.message}")
            }
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