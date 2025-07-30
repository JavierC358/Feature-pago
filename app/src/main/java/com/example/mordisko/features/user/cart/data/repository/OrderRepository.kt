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
            val firebaseTimestamp = Timestamp(now)

            // Mapear los items a un formato JSON-friendly
            val itemsMap = order.items.map { item ->
                mapOf(
                    "name" to item.name,
                    "quantity" to item.quantity,
                    "priceUsd" to item.priceUsd,
                    "size" to item.size,
                    "imageUrl" to item.imageUrl
                )
            }

            val orderWithMeta = hashMapOf(
                "orderNumber" to orderNumber,
                "userId" to userId,
                "items" to itemsMap,
                "deliveryOption" to order.deliveryOption,
                "address" to order.address,
                "reference" to order.reference,
                "paymentMethod" to order.paymentMethod,
                "exchangeRate" to order.exchangeRate,
                "subtotalUsd" to order.subtotalUsd,
                "deliveryCostUsd" to order.deliveryCostUsd,
                "totalUsd" to order.totalUsd,
                "totalBs" to order.totalBs,
                "timestamp" to firebaseTimestamp,
                "comment" to order.comment,
                "deseaFactura" to order.deseaFactura,
                "razonSocial" to order.razonSocial,
                "rif" to order.rif,
                "direccion" to order.direccion,
                "paymentStatus" to "pendiente" // aseguramos el campo aquí
            )

            ordersCollection.document(orderNumber).set(orderWithMeta).await()
            Result.success(orderNumber)

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
            return nextNumber.toString().padStart(6, '0')
        }
    }

    suspend fun ensureCounterInitialized() {
        val snapshot = counterDoc.get().await()
        if (!snapshot.exists()) {
            counterDoc.set(mapOf("value" to 0L)).await()
        }
    }
}