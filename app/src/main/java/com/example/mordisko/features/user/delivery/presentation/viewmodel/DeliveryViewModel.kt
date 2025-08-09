package com.example.mordisko.features.user.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryOption
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

data class DeliverySettings(
    val baseKm: Double = 3.0,     // radio corto
    val priceBase: Double = 2.0,  // USD dentro del radio
    val priceExtra: Double = 3.0, // USD fuera del radio
    val storeLat: Double = 0.0,
    val storeLng: Double = 0.0,
    val maxKm: Double = 12.0      // ✅ Máxima cobertura
)

class DeliveryViewModel : ViewModel() {

    private val _selectedOption = MutableStateFlow<DeliveryOption?>(null)
    val selectedOption: StateFlow<DeliveryOption?> = _selectedOption

    private val _settings = MutableStateFlow(DeliverySettings())
    val settings: StateFlow<DeliverySettings> = _settings

    fun onOptionSelected(option: DeliveryOption) {
        _selectedOption.value = option
    }

    // ✅ Carga desde Firestore: config/delivery_settings
    fun loadDeliverySettings() {
        viewModelScope.launch {
            try {
                val snap = FirebaseFirestore.getInstance()
                    .collection("config")
                    .document("delivery_settings")
                    .get()
                    .await()

                // Helper para leer número aunque sea string
                fun readNumber(key1: String, key2: String? = null, def: Double): Double {
                    snap.getDouble(key1)?.let { return it }
                    if (key2 != null) snap.getDouble(key2)?.let { return it }
                    (snap.getString(key1) ?: (key2?.let { snap.getString(it) })).let { s ->
                        s?.toDoubleOrNull()?.let { return it }
                    }
                    return def
                }

                val baseKm     = readNumber("baseKm",     "radioKm",     3.0)   // radio corto
                val priceBase  = readNumber("priceBase",  "precioCorto", 2.0)   // USD dentro del radio
                val priceExtra = readNumber("priceExtra", "precioLargo", 3.0)   // USD fuera del radio
                val storeLat   = readNumber("storeLat",   null,          0.0)
                val storeLng   = readNumber("storeLng",   null,          0.0)
                val maxKm      = readNumber("maxKm",      "coverageKm", 12.0)   // ✅ opcional desde Firestore

                _settings.value = DeliverySettings(
                    baseKm = baseKm,
                    priceBase = priceBase,
                    priceExtra = priceExtra,
                    storeLat = storeLat,
                    storeLng = storeLng,
                    maxKm = maxKm
                )

                Log.d("DeliveryVM", "Settings: baseKm=$baseKm, priceBase=$priceBase, priceExtra=$priceExtra, maxKm=$maxKm, store=($storeLat,$storeLng)")
            } catch (e: Exception) {
                Log.e("DeliveryVM", "Error cargando settings", e)
                // deja defaults
            }
        }
    }

    // ✅ Público: distancia en km desde la tienda (0 si tienda no configurada)
    fun distanciaDesdeTienda(lat: Double, lng: Double): Double {
        val s = _settings.value
        if (s.storeLat == 0.0 && s.storeLng == 0.0) {
            Log.w("DeliveryVM", "storeLat/Lng = 0.0 — revisa Firestore")
            return 0.0
        }
        val tienda  = LatLng(s.storeLat, s.storeLng)
        val cliente = LatLng(lat, lng)
        return calcularDistanciaKm(tienda, cliente)
    }

    // ✅ Público: en cobertura (≤ maxKm)
    fun estaEnCobertura(lat: Double, lng: Double): Boolean {
        val dKm = distanciaDesdeTienda(lat, lng)
        return dKm <= _settings.value.maxKm
    }

    // ✅ Costo: 2 USD si distancia ≤ baseKm; 3 USD si > baseKm
    fun calcularCostoDelivery(lat: Double, lng: Double): Double {
        val s = _settings.value

        // seguridad: si la tienda no está configurada, trata como tarifa base
        if (s.storeLat == 0.0 && s.storeLng == 0.0) {
            Log.w("DeliveryVM", "storeLat/Lng = 0.0 — revisa Firestore")
            return s.priceBase
        }

        val dKm = distanciaDesdeTienda(lat, lng)
        val usd = if (dKm <= s.baseKm) s.priceBase else s.priceExtra
        Log.d("DeliveryVM", "dist=${"%.3f".format(dKm)} km, baseKm=${s.baseKm} → $usd USD")
        return usd
    }

    // Haversine en km
    private fun calcularDistanciaKm(a: LatLng, b: LatLng): Double {
        val R = 6371.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)

        val h = sin(dLat / 2).pow(2.0) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)

        return 2 * R * asin(sqrt(h))
    }
}

enum class DeliveryOption { Moto, EnTienda, Retiro }