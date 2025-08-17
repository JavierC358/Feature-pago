package com.example.mordisko.features.admin.categories.data

import com.example.mordisko.features.admin.categories.domain.Category
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toCategory(): Category? {
    val d = this.toObject(CategoryDto::class.java) ?: return null
    return Category(
        id = this.id,
        name = d.name,
        imageUrl = d.imageUrl,
        visible = d.visible,
        position = d.position
    )
}