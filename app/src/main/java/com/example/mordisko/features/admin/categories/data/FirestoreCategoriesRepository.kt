package com.example.mordisko.features.admin.categories.data

import com.example.mordisko.features.admin.categories.domain.CategoriesRepository
import com.example.mordisko.features.admin.categories.domain.Category
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCategoriesRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CategoriesRepository {

    private val col get() = db.collection("categories")

    override fun listenVisibleOrdered(onChange: (List<Category>) -> Unit): ListenerRegistration {
        return col.whereEqualTo("visible", true)
            .orderBy("position")
            .addSnapshotListener { snap, _ ->
                val items = snap?.documents?.mapNotNull { it.toCategory() } ?: emptyList()
                onChange(items)
            }
    }

    override fun listenAllOrdered(onChange: (List<Category>) -> Unit): ListenerRegistration {
        return col.orderBy("position")
            .addSnapshotListener { snap, _ ->
                val items = snap?.documents?.mapNotNull { it.toCategory() } ?: emptyList()
                onChange(items)
            }
    }

    override suspend fun create(
        name: String, imageUrl: String, visible: Boolean, position: Int
    ): String {
        val ref = col.add(
            CategoryDto(
                name = name,
                imageUrl = imageUrl,
                visible = visible,
                position = position,
                updatedAt = Timestamp.now()
            )
        ).await()
        return ref.id
    }

    override suspend fun updateName(id: String, name: String) {
        col.document(id).update(
            mapOf("name" to name, "updatedAt" to Timestamp.now())
        ).await()
    }

    override suspend fun updateImageUrl(id: String, imageUrl: String) {
        col.document(id).update(
            mapOf("imageUrl" to imageUrl, "updatedAt" to Timestamp.now())
        ).await()
    }

    override suspend fun updateVisibility(id: String, visible: Boolean) {
        col.document(id).update(
            mapOf("visible" to visible, "updatedAt" to Timestamp.now())
        ).await()
    }

    override suspend fun updatePositions(orderedIds: List<String>) {
        val batch = db.batch()
        orderedIds.forEachIndexed { index, id ->
            batch.update(col.document(id), mapOf("position" to index + 1, "updatedAt" to Timestamp.now()))
        }
        batch.commit().await()
    }
}