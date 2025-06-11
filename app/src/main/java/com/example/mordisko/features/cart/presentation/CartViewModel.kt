package com.example.mordisko.features.cart.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mordisko.features.cart.domain.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addItem(item: CartItem) {
        Log.d("CartViewModel", "Añadiendo item: $item")
        _cartItems.value = _cartItems.value + item
    }

    fun removeItem(item: CartItem) {
        _cartItems.value = _cartItems.value - item
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun calculateSubtotal(): Double {
        return _cartItems.value.sumOf { it.priceUsd }
    }

    fun getItem(index: Int): CartItem? {
        return _cartItems.value.getOrNull(index)
    }

    fun itemCount(): Int = _cartItems.value.size
}
