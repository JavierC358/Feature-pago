package com.example.mordisko.features.menu.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mordisko.features.menu.domain.model.PizzaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    // 🍕 Pizza actualmente seleccionada
    private val _selectedPizza = MutableStateFlow<PizzaItem?>(null)
    val selectedPizza: StateFlow<PizzaItem?> = _selectedPizza

    fun selectPizza(pizza: PizzaItem) {
        Log.d("MenuViewModel", "Pizza seleccionada: ${pizza.name}")
        _selectedPizza.value = pizza
    }

    fun clearSelectedPizza() {
        _selectedPizza.value = null
    }
}