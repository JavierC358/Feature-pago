package com.example.mordisko.features.user.menu.domain.util

import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory

// 🔁 String → PizzaItemCategory
fun mapStringToPizzaItemCategory(category: String): PizzaItemCategory {
    return when (category.trim().lowercase()) {
        "pizzas" -> PizzaItemCategory.PIZZAS
        "calzone" -> PizzaItemCategory.CALZONE
        "dedos de queso" -> PizzaItemCategory.DEDOS_DE_QUESO
        "rolls de pizzas" -> PizzaItemCategory.ROLLS
        "extras" -> PizzaItemCategory.EXTRAS
        "postres" -> PizzaItemCategory.POSTRES
        "bebidas" -> PizzaItemCategory.BEBIDAS
        else -> throw IllegalArgumentException("Categoría no válida: $category")
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