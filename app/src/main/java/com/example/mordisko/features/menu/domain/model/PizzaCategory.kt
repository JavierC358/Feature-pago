package com.example.mordisko.features.menu.domain.model

import androidx.annotation.DrawableRes
import com.example.mordisko.R

data class PizzaCategory(
    val name: String,
    @DrawableRes val imageRes: Int
)

val pizzaCategories = listOf(
    PizzaCategory("Pizzas", R.drawable.ic_pizzahome_background),
    PizzaCategory("Calzone", R.drawable.ic_calzone_background),
    PizzaCategory("Dedos de Queso", R.drawable.ic_dedos_queso_background),
    PizzaCategory("Rolls de Pizzas", R.drawable.ic_rolls_background),
    PizzaCategory("Extras", R.drawable.ic_ingredientes_background),
    PizzaCategory("Postres", R.drawable.ic_postre_background),
    PizzaCategory("Bebidas", R.drawable.ic_refrescos_background)
)