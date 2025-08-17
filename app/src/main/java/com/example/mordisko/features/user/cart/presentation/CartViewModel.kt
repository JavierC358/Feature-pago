package com.example.mordisko.features.user.cart.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.cart.data.repository.OrderRepository
import com.example.mordisko.features.user.cart.domain.model.CartItem
import com.example.mordisko.features.user.cart.domain.model.OrderModel
import com.example.mordisko.features.user.cart.domain.model.SelectedExtra
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryOption
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

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

    private val _exchangeRate = MutableStateFlow(0.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    private val _orderComment = MutableStateFlow("")
    val orderComment: StateFlow<String> = _orderComment

    private val _selectedLat = MutableStateFlow<Double?>(null)
    val selectedLat: StateFlow<Double?> = _selectedLat

    private val _selectedLng = MutableStateFlow<Double?>(null)
    val selectedLng: StateFlow<Double?> = _selectedLng

    private val _deliveryCost = MutableStateFlow(0.0)
    val deliveryCost: StateFlow<Double> = _deliveryCost

    init {
        loadExchangeRate() // ✅ Carga automática al iniciar
    }

    // --- Métodos relacionados con dirección y delivery ---
    fun setCoordinates(lat: Double, lng: Double) {
        Log.d("MapDebug", "Coordenadas actualizadas: $lat, $lng")
        _selectedLat.value = lat
        _selectedLng.value = lng
    }

    fun setDeliveryCost(cost: Double) {
        if (_deliveryOption.value == DeliveryOption.Moto) {
            _deliveryCost.value = maxOf(2.0, cost) // ✅ Nunca menos de 2$
        } else {
            _deliveryCost.value = cost
        }
    }

    fun clearDeliveryData() {
        _selectedLat.value = null
        _selectedLng.value = null
        _deliveryCost.value = 0.0
    }

    fun setDeliveryOption(option: DeliveryOption) {
        _deliveryOption.value = option
        // ✅ Al elegir Moto, aseguramos costo mínimo
        if (option == DeliveryOption.Moto && _deliveryCost.value == 0.0) {
            _deliveryCost.value = 2.0
        }
    }

    fun setMainLocation(location: LatLng?) {
        _mainLocation.value = location
    }

    fun setSecondaryAddress(address: String) {
        _secondaryAddress.value = address
    }

    fun onAddressReferenceChanged(value: String) {
        _addressReference.value = value
    }

    // --- Métodos relacionados con el carrito ---
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

    // --- Comentarios y pago ---
    fun setOrderComment(comment: String) {
        _orderComment.value = comment
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    // --- Procesar pedido ---
    fun placeOrder(
        exchangeRate: Double,
        deliveryCostUsd: Double,
        clearCartOnSuccess: Boolean = true,
        onResult: (success: Boolean, error: String?, orderNumber: String?) -> Unit
    ) {
        val items = _cartItems.value
        val delivery = _deliveryOption.value
        val address = _secondaryAddress.value
        val reference = _addressReference.value
        val payment = _paymentMethod.value

        if (items.isEmpty()) {
            onResult(false, "El carrito está vacío", null)
            return
        }

        val subtotalUsd = items.sumOf { it.priceUsd * it.quantity }
        val totalUsd = subtotalUsd + deliveryCostUsd
        val totalBs = totalUsd * exchangeRate

        val order = OrderModel(
            items = items,
            deliveryOption = delivery?.name ?: "",
            address = address,
            reference = reference,
            paymentMethod = payment?.name ?: "",
            exchangeRate = exchangeRate,
            subtotalUsd = subtotalUsd,
            deliveryCostUsd = deliveryCostUsd,
            totalUsd = totalUsd,
            totalBs = totalBs
        )

        viewModelScope.launch {
            val result = orderRepository.saveOrder(order)
            if (result.isSuccess) {
                if (clearCartOnSuccess) clearCart()
                val orderNumber = result.getOrNull()
                onResult(true, null, orderNumber)
            } else {
                onResult(false, result.exceptionOrNull()?.message, null)
            }
        }
    }

    // --- Cargar tasa de cambio ---
    fun loadExchangeRate() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("config").document("exchange_rate").get().await()
                val tasa = snapshot.getDouble("value")
                if (tasa != null) {
                    _exchangeRate.value = tasa
                    Log.d("CartViewModel", "Tasa cargada: $tasa")
                } else {
                    Log.w("CartViewModel", "No se encontró el campo 'value'")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error cargando tasa: ${e.message}")
            }
        }
    }

    fun cargarExchangeRateDesdeFirestore() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("config")
                    .document("exchange_rate")
                    .get()
                    .await()

                val tasa = snapshot.getDouble("usdToBs") ?: 0.0
                _exchangeRate.value = tasa
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error al cargar tasa de cambio", e)
            }
        }
    }

    // --- Repetir pedido ---
    fun repetirPedido(items: List<CartItem>) {
        viewModelScope.launch {
            _cartItems.value = items.toMutableList()
            _orderComment.value = ""
            _addressReference.value = ""
            _paymentMethod.value = null
            Log.d("CartViewModel", "✅ Pedido repetido con ${items.size} productos")
        }
    }

    fun removeExtra(item: CartItem, extra: SelectedExtra) {
        _cartItems.value = _cartItems.value.map { cartItem ->
            if (cartItem == item) {
                cartItem.copy(extras = cartItem.extras.filterNot { it == extra })
            } else {
                cartItem
            }
        }
    }
}



