package com.example.mordisko.features.admin.data.repository

import android.util.Log
import com.example.mordisko.features.admin.domain.AdminMenuRepository
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminMenuRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminMenuRepository {

    override suspend fun getAllProducts(): List<PizzaItem> {
        val docs = firestore.collection("products")
            .get()
            .await()
            .documents

        Log.d("FirestoreDebug", "🔥 Documentos recibidos: ${docs.size}")

        return docs.mapNotNull {
            try {
                val item = it.toObject(PizzaItem::class.java)?.copy(id = it.id)
                Log.d("FirestoreDebug", "✅ Producto parseado: ${item?.name}")
                item
            } catch (e: Exception) {
                Log.e("FirestoreDebug", "❌ Error al parsear producto ${it.id}: ${e.message}")
                null
            }
        }
    }

    override suspend fun updateProduct(product: PizzaItem) {
        firestore.collection("products")
            .document(product.id)
            .set(product)
            .await()
    }
}