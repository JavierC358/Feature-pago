package com.example.mordisko.features.user.delivery.domain.model

data class DeliverySettings(
    val baseKm: Double = 5.0,
    val priceBase: Double = 2.0,
    val priceExtra: Double = 3.0,
    val storeLat: Double = 0.0,
    val storeLng: Double = 0.0
)

