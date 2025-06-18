package com.example.mordisko.features.cart.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class DeliveryOption {
    Moto, EnTienda, Retiro
}

@HiltViewModel
class DeliveryViewModel @Inject constructor() : ViewModel() {

    private val _selectedOption = MutableStateFlow<DeliveryOption?>(null)
    val selectedOption: StateFlow<DeliveryOption?> = _selectedOption

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    fun onOptionSelected(option: DeliveryOption) {
        _selectedOption.value = option
        if (option != DeliveryOption.Moto) {
            _address.value = ""
        }
    }

    fun onAddressChanged(newAddress: String) {
        _address.value = newAddress
    }
}