package com.example.mordisko.features.user.menu.domain.model

enum class PizzaItemCategory {
    PIZZAS,
    CARNE_EN_VARA,
    AHUMADOS,
    A_LA_BROASTER,
    EXTRAS,
    CACHAPAS,
    BEBIDAS,
    PEPITOS;

    companion object

}

fun PizzaItemCategory.Companion.valueOfOrNull(name: String): PizzaItemCategory? {
    return try {
        PizzaItemCategory.valueOf(
            name.trim()
                .replace(" ", "_")   // 🔹 ahora conserva guion bajo
                .replace("-", "_")   // 🔹 también guiones normales
                .replace("/", "_")   // 🔹 y slashes
                .uppercase()
        )
    } catch (_: Exception) {
        null
    }
}
