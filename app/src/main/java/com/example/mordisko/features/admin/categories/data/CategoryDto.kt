package com.example.mordisko.features.admin.categories.data

import com.google.firebase.Timestamp

data class CategoryDto(
    val name: String = "",
    val imageUrl: String = "",
    val visible: Boolean = true,
    val position: Int = 0,
    val updatedAt: Timestamp = Timestamp.now()
)