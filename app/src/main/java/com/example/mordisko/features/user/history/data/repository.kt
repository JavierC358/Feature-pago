package com.example.mordisko.features.user.history.data

import android.util.Log
import com.example.mordisko.features.user.cart.domain.model.CartItem
import com.example.mordisko.features.user.history.domain.model.OrderHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class OrdersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun getOrdersPage(
        pageSize: Long,
        lastDoc: DocumentSnapshot?
    ): Pair<List<OrderHistoryItem>, DocumentSnapshot?> = suspendCancellableCoroutine { cont ->

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("OrdersRepository", "Usuario no autenticado → lista vacía")
            if (cont.isActive) cont.resume(Pair(emptyList(), null))
            return@suspendCancellableCoroutine
        }

        var query = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize)

        if (lastDoc != null) {
            query = query.startAfter(lastDoc)
            Log.d("OrdersRepository", "Pidiendo página siguiente desde lastDoc=${lastDoc.id}, pageSize=$pageSize")
        } else {
            Log.d("OrdersRepository", "Pidiendo primera página, pageSize=$pageSize")
        }

        query.get()
            .addOnSuccessListener { snap ->
                try {
                    val docs = snap.documents
                    Log.d("OrdersRepository", "Snapshot ok: size=${docs.size}")

                    if (docs.isEmpty()) {
                        if (cont.isActive) cont.resume(Pair(emptyList(), null))
                        return@addOnSuccessListener
                    }

                    val list = docs.mapNotNull { doc ->
                        try {
                            val itemsList = (doc.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
                                val quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 0
                                val priceUsd = (itemMap["priceUsd"] as? Number)?.toDouble() ?: 0.0

                                CartItem(
                                    name = itemMap["name"] as? String ?: "",
                                    quantity = quantity,
                                    priceUsd = priceUsd,
                                    size = itemMap["size"] as? String ?: "",
                                    imageUrl = itemMap["imageUrl"] as? String ?: "",
                                    extras = emptyList()
                                )
                            } ?: emptyList()

                            val totalUsd = (doc.getDouble("totalUsd")
                                ?: (doc.get("totalUsd") as? Number)?.toDouble()
                                ?: 0.0)

                            val totalBs = (doc.getDouble("totalBs")
                                ?: (doc.get("totalBs") as? Number)?.toDouble()
                                ?: 0.0)

                            OrderHistoryItem(
                                orderNumber = doc.getString("orderNumber") ?: "",
                                timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L,
                                totalUsd = totalUsd,
                                totalBs = totalBs,
                                paymentMethod = doc.getString("paymentMethod") ?: "",
                                paymentStatus = doc.getString("paymentStatus") ?: "Desconocido",
                                items = itemsList
                            )
                        } catch (e: Exception) {
                            Log.w("OrdersRepository", "Error parseando doc=${doc.id}: ${e.message}")
                            null
                        }
                    }

                    val newLast = docs.lastOrNull()
                    Log.d("OrdersRepository", "Devuelvo list=${list.size}, lastDoc=${newLast?.id}")
                    if (cont.isActive) cont.resume(Pair(list, newLast))
                } catch (e: Exception) {
                    Log.e("OrdersRepository", "onSuccess pero error parseo: ${e.message}", e)
                    if (cont.isActive) cont.resume(Pair(emptyList(), null))
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrdersRepository", "Fallo query: ${e.message}", e)
                if (cont.isActive) cont.resume(Pair(emptyList(), null))
            }
    }

    suspend fun getOrdersCount(): Long = suspendCancellableCoroutine { cont ->
        val userId = auth.currentUser?.uid
        if (userId == null) { if (cont.isActive) cont.resume(0L); return@suspendCancellableCoroutine }

        firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .count() // 🔢 aggregate count
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { snapshot ->
                if (cont.isActive) cont.resume(snapshot.count)
            }
            .addOnFailureListener { e ->
                // si falla, no rompemos la UI
                if (cont.isActive) cont.resume(0L)
            }
    }
}