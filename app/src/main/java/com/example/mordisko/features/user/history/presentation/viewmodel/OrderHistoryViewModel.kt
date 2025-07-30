package com.example.mordisko.features.user.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mordisko.features.user.cart.domain.model.CartItem
import com.example.mordisko.features.user.history.domain.model.OrderHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderHistoryItem>>(emptyList())
    val orders: StateFlow<List<OrderHistoryItem>> = _orders

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchOrdersForCurrentUser()
    }

    private fun fetchOrdersForCurrentUser() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = "Error al obtener pedidos: ${error.message}"
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        // Recuperamos items de Firestore
                        val itemsList = (doc.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
                            CartItem(
                                name = itemMap["name"] as? String ?: "",
                                quantity = (itemMap["quantity"] as? Long)?.toInt() ?: 0,
                                priceUsd = (itemMap["priceUsd"] as? Double) ?: 0.0,
                                size = itemMap["size"] as? String ?: "",
                                imageUrl = itemMap["imageUrl"] as? String ?: "",
                                extras = emptyList() // si manejas extras, ajusta aquí
                            )
                        } ?: emptyList()

                        OrderHistoryItem(
                            orderNumber = doc.getString("orderNumber") ?: "",
                            timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L,
                            totalUsd = doc.getDouble("totalUsd") ?: 0.0,
                            totalBs = doc.getDouble("totalBs") ?: 0.0,
                            paymentMethod = doc.getString("paymentMethod") ?: "",
                            paymentStatus = doc.getString("paymentStatus") ?: "Desconocido",
                            items = itemsList
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                _orders.value = list
                _isLoading.value = false
            }
    }
}