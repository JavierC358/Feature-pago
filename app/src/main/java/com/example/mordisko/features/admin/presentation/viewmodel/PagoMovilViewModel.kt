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

data class PagoMovilData(
    val banco: String = "",
    val telefono: String = "",
    val cedula: String = "",
    val nombre: String = ""
)

@HiltViewModel
class PagoMovilViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _banco = MutableStateFlow("")
    val banco: StateFlow<String> = _banco.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _cedula = MutableStateFlow("")
    val cedula: StateFlow<String> = _cedula.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _isSaving = mutableStateOf(false)
    val isSaving: State<Boolean> = _isSaving

    fun cargarPagoMovil() {
        firestore.collection("pagomovil")
            .document("datos")
            .get()
            .addOnSuccessListener { snapshot ->
                val rawData = snapshot.toObject(PagoMovilData::class.java)
                rawData?.let {
                    _banco.value = it.banco
                    _telefono.value = it.telefono
                    _nombre.value = it.nombre
                    _cedula.value = it.cedula.toString() // ← conversión segura
                }
            }
            .addOnFailureListener {
                Log.e("PagoMovilViewModel", "Error al obtener datos de pago móvil", it)
            }
    }

    fun guardarPagoMovil() {
        _isSaving.value = true

        val data = PagoMovilData(
            banco = _banco.value,
            telefono = _telefono.value,
            cedula = _cedula.value,
            nombre = _nombre.value
        )

        firestore.collection("pago_movil_config")
            .document("datos")
            .set(data)
            .addOnSuccessListener {
                _isSaving.value = false
                // Opcional: mostrar mensaje de éxito con Snackbar o log
            }
            .addOnFailureListener {
                _isSaving.value = false
                // Opcional: mostrar error
            }
    }

    fun onBancoChange(newValue: String) {
        _banco.value = newValue
    }

    fun onTelefonoChange(newValue: String) {
        _telefono.value = newValue
    }

    fun onCedulaChange(newValue: String) {
        _cedula.value = newValue
    }

    fun onNombreChange(newValue: String) {
        _nombre.value = newValue
    }
}