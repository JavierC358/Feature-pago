package com.example.mordisko.features.user.menu.domain.model

import com.example.mordisko.features.admin.data.model.ProductDto
import com.example.mordisko.features.user.menu.domain.util.toPizzaItemCategory

fun ProductDto.toPizzaItem(): PizzaItem {
    return PizzaItem(
        name = name,
        description = description,
        imageRes = imageRes, // ❌ no convertimos aquí
        category = toPizzaItemCategory(category),
        priceUsd = priceUsd,
        priceBySize = priceBySize
    )
}