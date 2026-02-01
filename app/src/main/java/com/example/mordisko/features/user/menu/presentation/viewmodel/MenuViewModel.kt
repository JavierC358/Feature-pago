package com.example.mordisko.features.user.menu.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.cart.domain.model.SelectedExtra
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository // ✅ Añadimos esta línea
) : ViewModel() {

    private val _exchangeRate = MutableStateFlow(100.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    private var exchangeListener: ListenerRegistration? = null

    // 🍕 Productos del menú (cargados desde Firestore)
    private val _products = MutableStateFlow<List<PizzaItem>>(emptyList())
    val products: StateFlow<List<PizzaItem>> = _products

    // 🧀 Extras filtrados
    private val _extras = MutableStateFlow<List<PizzaItem>>(emptyList())
    val extras: StateFlow<List<PizzaItem>> = _extras

    // 🔥 Selección de peso (Carne en vara / Ahumados)
    private val _selectedWeightKey = MutableStateFlow("500")
    val selectedWeightKey: StateFlow<String> = _selectedWeightKey

    // 🔥 Selección de porción (Broaster)
    private val _selectedPortionKey = MutableStateFlow("HALF")
    val selectedPortionKey: StateFlow<String> = _selectedPortionKey

    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize

    fun selectSize(size: String) {
        _selectedSize.value = size
    }

    fun selectWeight(key: String) {
        _selectedWeightKey.value = key
    }

    fun selectPortion(key: String) {
        _selectedPortionKey.value = key
    }

    // ✅ Cargar productos al iniciar
    init {
        fetchProductsFromFirestore()

        viewModelScope.launch {
            products.collectLatest { lista ->
                _extras.value = lista.filter { it.category == PizzaItemCategory.EXTRAS } // ✅ Enum directamente
            }
        }

        observeExchangeRate()
    }

    private fun observeExchangeRate() {
        val docRef = FirebaseFirestore.getInstance()
            .collection("config")
            .document("exchange_rate")

        exchangeListener = docRef.addSnapshotListener { snap, err ->
            if (err != null) {
                err.printStackTrace()
                return@addSnapshotListener
            }
            if (snap == null || !snap.exists()) return@addSnapshotListener

            // ⚠️ Tu campo en Firestore se llama: usdToBs
            val n = snap.get("usdToBs") as? Number
            if (n != null && n.toDouble() > 0.0) {
                _exchangeRate.value = n.toDouble()
            }
        }
    }

    override fun onCleared() {
        exchangeListener?.remove()
        super.onCleared()
    }

    private fun fetchProductsFromFirestore() {
        viewModelScope.launch {
            repository.getAllProducts()
                .catch { e -> e.printStackTrace() }
                .collectLatest { productos ->
                    _products.value = productos.filter { it.visible }
                }
        }
    }

    // ✅ Todo lo que ya tenías sigue igual...

    private val _selectedPizza = MutableStateFlow<PizzaItem?>(null)
    val selectedPizza: StateFlow<PizzaItem?> = _selectedPizza

    fun selectPizza(pizza: PizzaItem) {
        _selectedPizza.value = pizza

        // 🔥 Aplicar tamaño default según categoría
        val defaultSize = getDefaultSizeForCategory(pizza.category)
        if (defaultSize != null) {
            _selectedSize.value = defaultSize   // 👈 ESTA LÍNEA ES LA CLAVE
        }
    }
    fun clearSelectedPizza() { _selectedPizza.value = null }

    private fun getDefaultSizeForCategory(category: PizzaItemCategory?): String? {
        return when (category) {
            PizzaItemCategory.CARNE_EN_VARA -> "0.500 kg"
            PizzaItemCategory.AHUMADOS -> "0.500 kg"
            PizzaItemCategory.A_LA_BROASTER -> "1/2"
            else -> null
        }
    }

    private val _selectedExtras = MutableStateFlow<Map<String, List<SelectedExtra>>>(emptyMap())
    val selectedExtras: StateFlow<Map<String, List<SelectedExtra>>> = _selectedExtras

    fun addExtra(extra: SelectedExtra) {
        val pizzaName = _selectedPizza.value?.name ?: return
        val currentExtras = _selectedExtras.value[pizzaName] ?: emptyList()
        _selectedExtras.value = _selectedExtras.value.toMutableMap().apply {
            put(pizzaName, currentExtras + extra)
        }
    }

    fun getExtrasForCurrentPizza(): List<SelectedExtra> {
        val pizzaName = _selectedPizza.value?.name ?: return emptyList()
        return _selectedExtras.value[pizzaName] ?: emptyList()
    }

    fun isExtraSelected(name: String, size: String): Boolean {
        val pizzaName = _selectedPizza.value?.name ?: return false
        return _selectedExtras.value[pizzaName]?.any { it.name == name && it.size == size } == true
    }

    fun toggleExtraSelection(name: String, size: String, priceUsd: Double) {
        val pizzaName = _selectedPizza.value?.name ?: return
        val currentExtras = _selectedExtras.value[pizzaName] ?: emptyList()
        val exists = currentExtras.any { it.name == name && it.size == size }

        val updatedExtras = if (exists) {
            currentExtras.filterNot { it.name == name && it.size == size }
        } else {
            currentExtras + SelectedExtra(name, size, priceUsd)
        }

        _selectedExtras.value = _selectedExtras.value.toMutableMap().apply {
            put(pizzaName, updatedExtras)
        }
    }

    fun removeExtra(name: String, size: String) {
        val pizzaName = _selectedPizza.value?.name ?: return
        val currentExtras = _selectedExtras.value[pizzaName] ?: return
        _selectedExtras.value = _selectedExtras.value.toMutableMap().apply {
            put(pizzaName, currentExtras.filterNot { it.name == name && it.size == size })
        }
    }

    fun clearExtras() {
        val pizzaName = _selectedPizza.value?.name ?: return
        _selectedExtras.value = _selectedExtras.value.toMutableMap().apply {
            put(pizzaName, emptyList())
        }
    }

    fun setDefaultSelectionForCategory(category: PizzaItemCategory) {
        when (category) {
            PizzaItemCategory.CARNE_EN_VARA,
            PizzaItemCategory.AHUMADOS -> _selectedWeightKey.value = "500"

            PizzaItemCategory.A_LA_BROASTER -> _selectedPortionKey.value = "HALF"

            else -> {}
        }
    }
}