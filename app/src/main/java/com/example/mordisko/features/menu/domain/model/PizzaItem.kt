package com.example.mordisko.features.menu.domain.model

import androidx.annotation.DrawableRes

data class PizzaItem(
    val name: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val category: PizzaItemCategory,
    val priceBySize: Map<String, Double>? = null, // ✅ precios por tamaño (opcional)
    val priceUsd: Double? = null // ✅ precio fijo para otras categorías (opcional)
)