package com.example.mordisko.features.cart.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.cart.data.repository.OrderRepository
import com.example.mordisko.features.cart.domain.model.CartItem
import com.example.mordisko.features.cart.domain.model.OrderModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _deliveryOption = MutableStateFlow<DeliveryOption?>(null)
    val deliveryOption: StateFlow<DeliveryOption?> = _deliveryOption

    private val _mainLocation = MutableStateFlow<LatLng?>(null)
    val mainLocation: StateFlow<LatLng?> = _mainLocation

    private val _secondaryAddress = MutableStateFlow("")
    val secondaryAddress: StateFlow<String> = _secondaryAddress

    private val _addressReference = MutableStateFlow("")
    val addressReference: StateFlow<String> = _addressReference

    private val _paymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val paymentMethod: StateFlow<PaymentMethod?> = _paymentMethod

    fun setPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    fun onAddressReferenceChanged(value: String) {
        _addressReference.value = value
    }

    fun setDeliveryOption(option: DeliveryOption) {
        _deliveryOption.value = option
    }

    fun setMainLocation(location: LatLng?) {
        _mainLocation.value = location
    }

    fun setSecondaryAddress(address: String) {
        _secondaryAddress.value = address
    }

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

    fun placeOrder(
        exchangeRate: Double,
        deliveryCostUsd: Double,
        onResult: (Boolean, String?, String?) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(false, "Usuario no autenticado", null)
            return
        }

        val cartItemsList = cartItems.value
        val subtotalUsd = cartItemsList.sumOf { it.priceUsd * it.quantity }
        val totalUsd = subtotalUsd + deliveryCostUsd
        val totalBs = totalUsd * exchangeRate

        val order = OrderModel(
            orderNumber = "", // El repositorio lo genera
            userId = "",      // El repositorio lo coloca
            items = cartItemsList,
            deliveryOption = deliveryOption.value?.name ?: "No definido",
            address = secondaryAddress.value,
            reference = addressReference.value.ifBlank { null },
            paymentMethod = paymentMethod.value?.name ?: "No definido",
            exchangeRate = exchangeRate,
            subtotalUsd = subtotalUsd,
            deliveryCostUsd = deliveryCostUsd,
            totalUsd = totalUsd,
            totalBs = totalBs,
            timestamp = Timestamp.now()
        )

        viewModelScope.launch {
            val result = orderRepository.saveOrder(order)
            if (result.isSuccess) {
                clearCart()
                val orderNumber = result.getOrNull()
                onResult(true, null, orderNumber)
            } else {
                onResult(false, result.exceptionOrNull()?.message, null)
            }
        }
    }
}