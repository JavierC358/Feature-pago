package com.example.mordisko.features.user.menu.domain.repository

import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun getAllProducts(): Flow<List<PizzaItem>>
}