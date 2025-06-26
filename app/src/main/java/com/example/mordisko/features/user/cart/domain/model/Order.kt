package com.example.mordisko.features.user.cart.domain.model

import com.google.firebase.Timestamp

data class OrderModel(
    val orderNumber: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val deliveryOption: String = "",
    val address: String = "",
    val reference: String? = null,
    val paymentMethod: String = "",
    val exchangeRate: Double = 0.0,
    val subtotalUsd: Double = 0.0,
    val deliveryCostUsd: Double = 0.0,
    val totalUsd: Double = 0.0,
    val totalBs: Double = 0.0,
    val timestamp: Timestamp? = null
)
