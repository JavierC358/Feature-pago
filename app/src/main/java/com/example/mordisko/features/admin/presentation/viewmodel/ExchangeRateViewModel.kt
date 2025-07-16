package com.example.mordisko.features.admin.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate: StateFlow<Double?> = _exchangeRate

    fun getExchangeRate() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("config")
                    .document("exchange_rate")
                    .get()
                    .await()

                val tasa = snapshot.getDouble("usdToBs")
                _exchangeRate.value = tasa
                Log.d("ExchangeRateVM", "Tasa obtenida: $tasa")
            } catch (e: Exception) {
                Log.e("ExchangeRateVM", "Error al obtener la tasa", e)
            }
        }
    }

    fun guardarExchangeRate(nuevaTasa: Double) {
        viewModelScope.launch {
            try {
                firestore.collection("config")
                    .document("exchange_rate")
                    .set(
                        mapOf(
                            "usdToBs" to nuevaTasa,
                            "updatedAt" to Date()
                        )
                    )
                    .await()

                _exchangeRate.value = nuevaTasa
                Log.d("ExchangeRateVM", "Tasa guardada: $nuevaTasa")
            } catch (e: Exception) {
                Log.e("ExchangeRateVM", "Error al guardar la tasa", e)
            }
        }
    }
}