package com.example.mordisko.features.user.menu.domain.model

enum class PizzaItemCategory {
    PIZZAS,
    CALZONE,
    DEDOS_DE_QUESO,
    ROLLS,
    EXTRAS,
    POSTRES,
    BEBIDAS;

    companion object

}

fun PizzaItemCategory.Companion.valueOfOrNull(name: String): PizzaItemCategory? {
    return try {
        PizzaItemCategory.valueOf(
            name.replace(" ", "")
                .replace("-", "")
                .replace("/", "")
                .uppercase()
        )
    } catch (_: Exception) {
        null
    }
}
