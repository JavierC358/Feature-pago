package com.example.mordisko.features.admin.data.model

import android.content.Context
import android.util.Log
import com.example.mordisko.R
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.domain.util.toPizzaItemCategory

data class ProductDto(
    val name: String = "",
    val description: String = "",
    val imageRes: String = "", // 🔄 Antes era Int
    val category: String = "",
    val priceUsd: Double? = null,
    val priceBySize: Map<String, Double>? = null
)

fun ProductDto.toPizzaItem(context: Context): PizzaItem {
    val imageResId = context.resources.getIdentifier(imageRes, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.ic_placeholder

    val categoryEnum = toPizzaItemCategory(category)

    return PizzaItem(
        name = name,
        description = description,
        imageRes = imageRes,
        category = categoryEnum, // ✅ ahora sí usas el valor ya verificado
        priceUsd = priceUsd,
        priceBySize = priceBySize
    )
}