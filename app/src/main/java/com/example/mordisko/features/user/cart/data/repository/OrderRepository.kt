package com.example.mordisko.features.user.cart.data.repository

import com.example.mordisko.features.user.cart.domain.model.OrderModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val ordersCollection = firestore.collection("orders")
    private val counterDoc = firestore.collection("counters").document("orderCounter")

    suspend fun saveOrder(order: OrderModel): Result<String> {
        return try {
            val orderNumber = generateOrderNumber()
            val userId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val now = Date()
            val firebaseTimestamp = Timestamp(now.time / 1000, ((now.time % 1000) * 1000000).toInt())

            val orderWithMeta = order.copy(
                orderNumber = orderNumber,
                userId = userId,
                timestamp = firebaseTimestamp
            )

            firestore.collection("orders").document(orderNumber).set(orderWithMeta).await()
            Result.success(orderNumber) // 👈 MUY IMPORTANTE
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateOrderNumber(): String {
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterDoc)
            val current = snapshot.getLong("value") ?: 0L
            val next = current + 1
            transaction.update(counterDoc, "value", next)
            next
        }.await().let { nextNumber ->
            return nextNumber.toString().padStart(6, '0') // Ejemplo: 000001
        }
    }

    suspend fun ensureCounterInitialized() {
        val snapshot = counterDoc.get().await()
        if (!snapshot.exists()) {
            counterDoc.set(mapOf("value" to 0L)).await()
        }
    }
}