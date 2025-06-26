package com.example.mordisko.features.user.cart.data

import com.example.mordisko.features.user.cart.domain.model.OrderModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getNextOrderNumber(): String {
        val snapshot = firestore.collection("orders").get().await()
        val next = snapshot.size() + 1
        return String.format("%06d", next)
    }

    suspend fun saveOrder(order: OrderModel): Boolean {
        val user = auth.currentUser ?: return false
        val orderNumber = getNextOrderNumber()

        val now = Date()
        val timestamp = Timestamp(now.time / 1000, ((now.time % 1000) * 1000000).toInt())

        val finalOrder = order.copy(
            orderNumber = orderNumber,
            userId = user.uid,
            timestamp = timestamp
        )

        return try {
            firestore.collection("orders").document(orderNumber).set(finalOrder).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}