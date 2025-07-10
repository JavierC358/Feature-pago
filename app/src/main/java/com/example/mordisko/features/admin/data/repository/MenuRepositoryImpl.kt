package com.example.mordisko.features.admin.data.repository

import android.content.Context
import android.util.Log
import com.example.mordisko.features.admin.data.model.ProductDto
import com.example.mordisko.features.admin.data.model.toPizzaItem
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.menu.domain.model.toPizzaItem
import com.example.mordisko.features.user.menu.domain.repository.MenuRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : MenuRepository {

    override fun getAllProducts(): Flow<List<PizzaItem>> = callbackFlow {
        val listener = firestore.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(ProductDto::class.java)?.toPizzaItem(context)
                    } catch (e: Exception) {
                        Log.e("FirestoreParse", "❌ Documento ignorado: ${doc.id} - ${e.message}")
                        null
                    }
                } ?: emptyList()

                trySend(products).isSuccess
            }

        awaitClose { listener.remove() }
    }
}