package com.example.mordisko.features.user.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _paymentStatus = MutableStateFlow("")
    val paymentStatus: StateFlow<String> = _paymentStatus

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