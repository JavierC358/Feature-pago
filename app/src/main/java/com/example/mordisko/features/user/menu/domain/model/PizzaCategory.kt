package com.example.mordisko.features.user.menu.domain.model

import androidx.annotation.DrawableRes
import com.example.mordisko.R

data class PizzaCategory(
    val name: String,
    @DrawableRes val imageRes: Int,
    val enum: PizzaItemCategory
)

val pizzaCategories = listOf(
    PizzaCategory("Pizzas", R.drawable.ic_pizzas13_menu, PizzaItemCategory.PIZZAS),
    PizzaCategory("Calzone", R.drawable.ic_calzone3_background, PizzaItemCategory.CALZONE),
    PizzaCategory("Dedos de Queso", R.drawable.ic_dedos_queso_background, PizzaItemCategory.DEDOS_DE_QUESO),
    PizzaCategory("Rolls de Pizzas", R.drawable.ic_rolls_background, PizzaItemCategory.ROLLS),
    PizzaCategory("Postres", R.drawable.ic_postres_background, PizzaItemCategory.POSTRES),
    PizzaCategory("Bebidas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS)
)