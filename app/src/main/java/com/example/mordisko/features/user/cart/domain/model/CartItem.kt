package com.example.mordisko.features.user.cart.domain.model

data class CartItem(
    val name: String = "",
    val size: String = "",
    val quantity: Int = 0,
    val priceUsd: Double = 0.0,
    val extras: List<SelectedExtra> = emptyList(),
    val imageRes: Int = 0
)


