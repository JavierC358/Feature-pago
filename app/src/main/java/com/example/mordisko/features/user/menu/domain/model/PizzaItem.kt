package com.example.mordisko.features.user.menu.domain.model

data class PizzaItem(
    val name: String = "",
    val description: String = "",
    val imageRes: String = "", // 🔄 ahora es String, no Int
    val category: PizzaItemCategory = PizzaItemCategory.PIZZAS,
    val priceUsd: Double? = null,
    val priceBySize: Map<String, Double>? = null)