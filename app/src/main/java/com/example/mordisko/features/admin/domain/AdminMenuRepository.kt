package com.example.mordisko.features.admin.domain

import com.example.mordisko.features.user.menu.domain.model.PizzaItem

interface AdminMenuRepository {
    suspend fun getAllProducts(): List<PizzaItem>
    suspend fun updateProduct(product: PizzaItem)
}