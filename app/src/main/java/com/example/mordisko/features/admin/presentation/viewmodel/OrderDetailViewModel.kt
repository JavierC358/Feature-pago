package com.example.mordisko.features.admin.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.cart.domain.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _order = MutableStateFlow<OrderModel?>(null)
    val order: StateFlow<OrderModel?> = _order

    fun loadOrder(orderNumber: String) {
        viewModelScope.launch {
            try {
                Log.d("OrderDetailVM", "Buscando orden con ID(doc): $orderNumber")

                val doc = firestore.collection("orders")
                    .document(orderNumber)
                    .get()
                    .await()

                if (doc.exists()) {
                    val order = doc.toObject(OrderModel::class.java)
                    _order.value = order
                    Log.d("OrderDetailVM", "Orden encontrada: $order")
                } else {
                    Log.e("OrderDetailVM", "No existe la orden con ID(doc): $orderNumber")
                    _order.value = null
                }
            } catch (e: Exception) {
                Log.e("OrderDetailVM", "Error al cargar la orden", e)
                _order.value = null
            }
        }
    }
}
