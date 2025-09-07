package com.example.mordisko.features.admin.data.model

import android.content.Context
import android.util.Log
import com.example.mordisko.R
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.domain.util.toPizzaItemCategory

data class ProductDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val priceUsd: Double? = null,
    val priceBySize: Map<String, Double>? = null,
    val priceByWeight: Map<String, Double>? = null,
    val priceByPortion: Map<String, Double>? = null,
    val visible: Boolean = true
)

fun ProductDto.toPizzaItem(): PizzaItem {
    return PizzaItem(
        name = name,
        description = description,
        imageUrl = imageUrl, // ✅ reemplaza imageRes
        category = toPizzaItemCategory(category),
        priceUsd = priceUsd ?: 0.0,
        priceBySize = priceBySize ?: emptyMap(),
        priceByWeight = priceByWeight ?: emptyMap(),
        priceByPortion = priceByPortion ?: emptyMap(),
        visible = visible // 👈 asegúrate de propagar el campo visible
    )
}