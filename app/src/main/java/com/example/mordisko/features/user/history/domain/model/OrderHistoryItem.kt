package com.example.mordisko.features.user.history.domain.model

import com.example.mordisko.features.user.cart.domain.model.CartItem
import java.text.SimpleDateFormat
import java.util.*

data class OrderHistoryItem(
    val orderNumber: String,
    val timestamp: Long,
    val totalUsd: Double,
    val totalBs: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val items: List<CartItem> = emptyList() // 👈 agregamos los items
) {
    fun formattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}