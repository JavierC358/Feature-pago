package com.example.mordisko.features.user.cart.domain.model

data class SelectedExtra(
    val name: String = "",
    val size: String = "",
    val priceUsd: Double = 0.0,
    val priceBs: Double = 0.0
)