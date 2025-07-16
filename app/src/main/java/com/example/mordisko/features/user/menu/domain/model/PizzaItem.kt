package com.example.mordisko.features.user.menu.domain.model

data class PizzaItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "", // ✅ ahora con valor por defecto
    val category: PizzaItemCategory = PizzaItemCategory.PIZZAS,
    val priceUsd: Double = 0.0,
    val priceBySize: Map<String, Double> = emptyMap(),
    val priceBs: Double = 0.0,
    val visible: Boolean = true
)