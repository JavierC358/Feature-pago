package com.example.mordisko.features.admin.categories.domain

import com.google.firebase.firestore.ListenerRegistration

interface CategoriesRepository {
    fun listenVisibleOrdered(onChange: (List<Category>) -> Unit): ListenerRegistration
    fun listenAllOrdered(onChange: (List<Category>) -> Unit): ListenerRegistration
    suspend fun create(name: String, imageUrl: String = "", visible: Boolean = true, position: Int = 0): String
    suspend fun updateName(id: String, name: String)
    suspend fun updateImageUrl(id: String, imageUrl: String)
    suspend fun updateVisibility(id: String, visible: Boolean)
    suspend fun updatePositions(orderedIds: List<String>)
}