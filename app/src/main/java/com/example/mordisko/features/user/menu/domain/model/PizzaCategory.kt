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
    PizzaCategory("Carne en Vara", R.drawable.ic_calzone3_background, PizzaItemCategory.CARNE_EN_VARA),
    PizzaCategory("Ahumados", R.drawable.ic_dedos_queso_background, PizzaItemCategory.AHUMADOS),
    PizzaCategory("A la Broaster", R.drawable.ic_rolls_background, PizzaItemCategory.A_LA_BROASTER),
    PizzaCategory("Cachapas", R.drawable.ic_postres_background, PizzaItemCategory.CACHAPAS),
    PizzaCategory("Bebidas", R.drawable.ic_refrescos_background, PizzaItemCategory.BEBIDAS)
)