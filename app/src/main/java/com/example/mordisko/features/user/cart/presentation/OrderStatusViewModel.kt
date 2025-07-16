package com.example.mordisko.features.user.cart.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.admin.domain.model.PagoMovilData
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import javax.inject.Inject

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _paymentStatus = MutableStateFlow("")
    val paymentStatus: StateFlow<String> = _paymentStatus

    private val _pagoMovilData = mutableStateOf(PagoMovilData())
    val pagoMovilData: State<PagoMovilData> = _pagoMovilData

    fun cargarPagoMovil() {
        FirebaseFirestore.getInstance()
            .collection("pago_movil_config")
            .document("datos")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(PagoMovilData::class.java)?.let {
                    _pagoMovilData.value = it
                }
            }
    }

    fun monitorPaymentStatus(orderNumber: String) {
        viewModelScope.launch {
            val docRef = firestore.collection("orders").document(orderNumber)
            docRef.addSnapshotListener { snapshot, _ ->
                val status = snapshot?.getString("paymentStatus") ?: "pendiente"
                _paymentStatus.value = status
            }
        }
    }
}