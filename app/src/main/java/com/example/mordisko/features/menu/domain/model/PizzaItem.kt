package com.example.mordisko.features.menu.domain.model

import androidx.annotation.DrawableRes

data class PizzaItem(
    val name: String,
    val description: String,
    @DrawableRes val imageRes: Int
)