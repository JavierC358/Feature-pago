package com.example.mordisko.features.user.menu.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.cart.domain.model.SelectedExtra
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    // 🍕 Pizza seleccionada
    private val _selectedPizza = MutableStateFlow<PizzaItem?>(null)
    val selectedPizza: StateFlow<PizzaItem?> = _selectedPizza

    fun selectPizza(pizza: PizzaItem) {
        Log.d("MenuViewModel", "Pizza seleccionada: ${pizza.name}")
        _selectedPizza.value = pizza
    }

    fun clearSelectedPizza() {
        _selectedPizza.value = null
    }

    // 🧀 Extras seleccionados por nombre de pizza
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
}