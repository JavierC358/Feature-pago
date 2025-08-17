package com.example.mordisko.features.admin.categories.domain

data class Category(
    val id: String,
    val name: String,
    val imageUrl: String,
    val visible: Boolean,
    val position: Int
)