package com.example.mordisko.features.cart.data.repository

import com.example.mordisko.features.cart.domain.model.OrderModel
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

    suspend fun saveOrder(order: OrderModel): Result<String> {
        return try {
            val orderNumber = generateOrderNumber()
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val now = Date()
            val firebaseTimestamp = Timestamp(now.time / 1000, ((now.time % 1000) * 1000000).toInt())

            val orderWithMeta = order.copy(
                orderNumber = orderNumber,
                userId = userId,
                timestamp = firebaseTimestamp
            )

            ordersCollection.document(orderNumber).set(orderWithMeta).await()
            Result.success(orderNumber) // 👈 aquí devolvemos el número de orden
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateOrderNumber(): String {
        val snapshot = ordersCollection.orderBy("timestamp").get().await()
        val count = snapshot.size() + 1
        return count.toString().padStart(6, '0') // Ejemplo: 000001
    }
}