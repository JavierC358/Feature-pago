package com.example.mordisko.features.user.menu.domain.util

import android.util.Log
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory

// 🔁 String → PizzaItemCategory (seguro y tolerante a errores)
fun toPizzaItemCategory(category: String): PizzaItemCategory {
    val normalized = category.trim().lowercase().replace("_", " ").replace("-", " ")
    Log.d("🔥 Normalized Category", normalized)

    val map = mapOf(
        "pizzas" to PizzaItemCategory.PIZZAS,
        "calzone" to PizzaItemCategory.CALZONE,
        "dedos de queso" to PizzaItemCategory.DEDOS_DE_QUESO,
        "dedos" to PizzaItemCategory.DEDOS_DE_QUESO,
        "rolls" to PizzaItemCategory.ROLLS,
        "rolls de pizzas" to PizzaItemCategory.ROLLS,
        "extras" to PizzaItemCategory.EXTRAS,
        "postres" to PizzaItemCategory.POSTRES,
        "bebidas" to PizzaItemCategory.BEBIDAS
    )

    return map[normalized] ?: PizzaItemCategory.PIZZAS.also {
        Log.e("❌ CategoryMapper", "Categoría inválida: '$category' → '$normalized'. Se usará PIZZAS por defecto.")
    }
}

// 🔁 PizzaItemCategory → String
fun mapPizzaItemCategoryToString(category: PizzaItemCategory): String {
    return when (category) {
        PizzaItemCategory.PIZZAS -> "Pizzas"
        PizzaItemCategory.CALZONE -> "Calzone"
        PizzaItemCategory.DEDOS_DE_QUESO -> "Dedos de Queso"
        PizzaItemCategory.ROLLS -> "Rolls de Pizzas"
        PizzaItemCategory.EXTRAS -> "Extras"
        PizzaItemCategory.POSTRES -> "Postres"
        PizzaItemCategory.BEBIDAS -> "Bebidas"
    }
}