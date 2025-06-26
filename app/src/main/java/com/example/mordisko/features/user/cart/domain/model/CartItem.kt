package com.example.mordisko.features.user.cart.domain.model

data class CartItem(
    val name: String,
    val size: String,
    val quantity: Int,
    val imageRes: Int,
    val priceUsd: Double,
    val extras: List<SelectedExtra> = emptyList()
)