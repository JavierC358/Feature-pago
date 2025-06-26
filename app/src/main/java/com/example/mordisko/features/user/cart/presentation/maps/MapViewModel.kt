package com.example.mordisko.features.user.cart.presentation.maps

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _mainLocation = MutableStateFlow<LatLng?>(null)
    val mainLocation: StateFlow<LatLng?> = _mainLocation

    private val _secondaryAddress = MutableStateFlow("")
    val secondaryAddress: StateFlow<String> = _secondaryAddress

    fun setMainLocation(location: LatLng?) {
        _mainLocation.value = location
    }

    fun onSecondaryAddressChanged(address: String) {
        _secondaryAddress.value = address
    }
}