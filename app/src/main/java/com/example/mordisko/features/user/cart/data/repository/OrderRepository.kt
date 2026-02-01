package com.example.mordisko.features.user.cart.data.repository

import android.util.Log
import com.example.mordisko.features.user.cart.domain.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val orderRequestsCollection = firestore.collection("order_requests")
    private val ordersCollection = firestore.collection("orders")
    private val counterDoc = firestore.collection("counters").document("orderCounter")

    suspend fun saveOrder(order: OrderModel, requestId: String): Result<String> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val itemsMap = order.items.map { item ->
                val extrasMap = item.extras.map { extra ->
                    mapOf(
                        "name" to extra.name,
                        "size" to extra.size,
                        "priceUsd" to extra.priceUsd,
                        "priceBs" to extra.priceBs
                    )
                }

                mapOf(
                    "name" to item.name,
                    "quantity" to item.quantity,
                    "priceUsd" to item.priceUsd,
                    "size" to item.size,
                    "imageUrl" to item.imageUrl,
                    "extras" to extrasMap
                )
            }

            val requestRef = orderRequestsCollection.document(requestId)

            val orderNumber = firestore.runTransaction { transaction ->

                // ✅ 1) Idempotencia: si ya existe requestId, devolvemos mismo orderNumber
                val existingReqSnap = transaction.get(requestRef)
                val existingOrderNumber = existingReqSnap.getString("orderNumber")
                if (!existingOrderNumber.isNullOrBlank()) {
                    return@runTransaction existingOrderNumber
                }

                // ✅ 2) Correlativo en la misma transacción (solo una vez)
                val counterSnap = transaction.get(counterDoc)
                val current = counterSnap.getLong("value") ?: 0L
                val next = current + 1
                transaction.update(counterDoc, "value", next)
                val newOrderNumber = next.toString().padStart(6, '0')

                // ✅ 3) Crear orden
                val orderRef = ordersCollection.document(newOrderNumber)

                val orderWithMeta = hashMapOf(
                    "orderNumber" to newOrderNumber,
                    "requestId" to requestId,
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
                    "timestamp" to FieldValue.serverTimestamp(),
                    "comment" to order.comment,
                    "deseaFactura" to order.deseaFactura,
                    "razonSocial" to order.razonSocial,
                    "rif" to order.rif,
                    "direccion" to order.direccion,
                    "paymentStatus" to "pendiente",
                    "customerLat" to order.customerLat,
                    "customerLng" to order.customerLng
                )

                transaction.set(orderRef, orderWithMeta)

                // ✅ 4) Sellar requestId -> orderNumber
                val requestData = mapOf(
                    "orderNumber" to newOrderNumber,
                    "userId" to userId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                transaction.set(requestRef, requestData, SetOptions.merge())

                newOrderNumber
            }.await()

            Result.success(orderNumber)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ensureCounterInitialized() {
        val snapshot = counterDoc.get().await()
        if (!snapshot.exists()) {
            counterDoc.set(mapOf("value" to 0L)).await()
        }
    }

    suspend fun submitPaymentVerification(
        orderNumber: String,
        amountBsInput: String,
        referenceLast4: String,
        payerPhone: String,
        razonSocial: String?,
        rif: String?,
        direccion: String?
    ): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: ""

            val amount = amountBsInput.replace(',', '.').toDoubleOrNull() ?: 0.0

            val batch = firestore.batch()
            val orderRef = ordersCollection.document(orderNumber)
            val infoRef = orderRef.collection("payment_verification").document("info")

            // 1) estado principal
            batch.update(orderRef, mapOf("paymentStatus" to "por_verificar"))

            // 2) (opcional) datos de factura
            val factura = mutableMapOf<String, Any>()
            if (!razonSocial.isNullOrBlank() || !rif.isNullOrBlank() || !direccion.isNullOrBlank()) {
                factura["deseaFactura"] = true
                razonSocial?.let { factura["razonSocial"] = it }
                rif?.let { factura["rif"] = it }
                direccion?.let { factura["direccion"] = it }
            }
            if (factura.isNotEmpty()) batch.update(orderRef, factura)

            // 3) subcolección para el admin
            val infoData = mapOf(
                "amountPaid" to amount,
                "referenceLast4" to referenceLast4.trim(),
                "phoneNumber" to payerPhone.trim(),
                "status" to "por_verificar",
                "submittedAt" to FieldValue.serverTimestamp(),
                "submittedBy" to uid
            )
            batch.set(infoRef, infoData, SetOptions.merge())

            Log.d("OrderRepo", "submitPaymentVerification: COMMIT… $orderNumber")
            batch.commit().await()
            Log.d("OrderRepo", "submitPaymentVerification: OK $orderNumber")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OrderRepo", "submitPaymentVerification: ERROR $orderNumber", e)
            Result.failure(e)
        }
    }
}