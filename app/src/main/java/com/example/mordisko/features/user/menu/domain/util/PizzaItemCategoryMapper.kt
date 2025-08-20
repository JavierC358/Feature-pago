package com.example.mordisko.features.user.menu.domain.util

import android.util.Log
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory

// 🔁 String → PizzaItemCategory (seguro y tolerante a errores)
fun toPizzaItemCategory(category: String): PizzaItemCategory {
    val normalized = category.trim().lowercase().replace("_", " ").replace("-", " ")
    Log.d("🔥 Normalized Category", normalized)

    val map = mapOf(
        "pizzas" to PizzaItemCategory.PIZZAS,
        "carne en vara" to PizzaItemCategory.CARNE_EN_VARA,
        "ahumados" to PizzaItemCategory.AHUMADOS,
        "a la broaster" to PizzaItemCategory.A_LA_BROASTER,
        "extras" to PizzaItemCategory.EXTRAS,
        "cachapas" to PizzaItemCategory.CACHAPAS,
        "bebidas" to PizzaItemCategory.BEBIDAS,
        "pepitos" to PizzaItemCategory.PEPITOS
    )

    return map[normalized] ?: PizzaItemCategory.PIZZAS.also {
        Log.e("❌ CategoryMapper", "Categoría inválida: '$category' → '$normalized'. Se usará PIZZAS por defecto.")
    }
}

// 🔁 PizzaItemCategory → String
fun mapPizzaItemCategoryToString(category: PizzaItemCategory): String {
    return when (category) {
        PizzaItemCategory.PIZZAS -> "Pizzas"
        PizzaItemCategory.CARNE_EN_VARA -> "Carne en Vara"
        PizzaItemCategory.AHUMADOS -> "Ahumados"
        PizzaItemCategory.A_LA_BROASTER -> "A la Broaster"
        PizzaItemCategory.EXTRAS -> "Extras"
        PizzaItemCategory.CACHAPAS -> "Cachapas"
        PizzaItemCategory.BEBIDAS -> "Bebidas"
        PizzaItemCategory.PEPITOS -> "Pepitos"
    }
}