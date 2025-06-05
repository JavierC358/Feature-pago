package com.example.mordisko.features.menu.domain.model

import androidx.annotation.DrawableRes
import com.example.mordisko.R

data class PizzaCategory(
    val name: String,
    @DrawableRes val imageRes: Int
)

val pizzaCategories = listOf(
    PizzaCategory("Pizzas", R.drawable.ic_burger_background),
    PizzaCategory("Dedos de Queso", R.drawable.ic_burger_background),
    PizzaCategory("Rolls de Pizzas", R.drawable.ic_burger_background),
    PizzaCategory("Extras", R.drawable.ic_burger_background),
    PizzaCategory("Postres", R.drawable.ic_burger_background),
    PizzaCategory("Bebidas", R.drawable.ic_burger_background)
)