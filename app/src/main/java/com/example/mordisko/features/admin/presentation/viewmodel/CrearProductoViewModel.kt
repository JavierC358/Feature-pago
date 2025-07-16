package com.example.mordisko.features.admin.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CrearProductoViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _categoria = MutableStateFlow("")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _precio = MutableStateFlow("")
    val precio: StateFlow<String> = _precio.asStateFlow()

    private val _imagen = MutableStateFlow("")
    val imagen: StateFlow<String> = _imagen.asStateFlow()

    private val _visible = MutableStateFlow(true)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _precioUsd = mutableStateOf(0.0)
    val precioUsd: State<Double> = _precioUsd

    private val _preciosPorTamanio = mutableStateOf<Map<String, Double>>(emptyMap())
    val preciosPorTamanio: State<Map<String, Double>> = _preciosPorTamanio

    private val _imagenRes = mutableStateOf("")
    val imagenRes: State<String> = _imagenRes

    fun onNombreChange(value: String) {
        _nombre.value = value
    }

    fun onDescripcionChange(value: String) {
        _descripcion.value = value
    }

    fun onCategoriaChange(value: String) {
        _categoria.value = value
    }

    fun onPrecioChange(value: String) {
        _precio.value = value
    }

    fun onImagenChange(value: String) {
        _imagen.value = value
    }

    fun onVisibleChange(value: Boolean) {
        _visible.value = value
    }

    fun onPrecioUsdChange(value: String) {
        _precioUsd.value = value.toDoubleOrNull() ?: 0.0
    }

    fun onPrecioPorTamanioChange(size: String, value: String) {
        val map = _preciosPorTamanio.value.toMutableMap()
        map[size] = value.toDoubleOrNull() ?: 0.0
        _preciosPorTamanio.value = map
    }

    fun onImagenResChange(value: String) {
        _imagenRes.value = value
    }

    fun guardarProducto(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.value.isBlank() || descripcion.value.isBlank() || categoria.value.isBlank()) {
            onError("Todos los campos son obligatorios.")
            return
        }

        val nuevoDoc = firestore.collection("products").document() // 🔑 ID único

        val productoMap = mapOf(
            "id" to nuevoDoc.id,
            "name" to nombre.value.trim(),
            "description" to descripcion.value.trim(),
            "category" to categoria.value.trim(),
            "visible" to visible.value,
            "priceUsd" to precioUsd.value,
            "priceBySize" to preciosPorTamanio.value,
            "imageRes" to imagenRes.value.trim()
        )

        Log.d("CrearProducto", "Producto a guardar: $productoMap") // ✅ Verifica si los valores llegan bien

        nuevoDoc.set(productoMap)
            .addOnSuccessListener {
                Log.d("CrearProducto", "✅ Producto guardado con éxito")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("CrearProducto", "❌ Error al guardar producto: ${e.message}")
                onError(e.message ?: "Error desconocido al guardar el producto")
            }
    }

}
