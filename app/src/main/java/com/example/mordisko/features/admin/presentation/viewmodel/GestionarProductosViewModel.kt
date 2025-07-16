package com.example.mordisko.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mordisko.features.user.menu.domain.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GestionarProductosViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    fun cargarProductos() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Producto::class.java)?.copy(id = doc.id)
                }
                _productos.value = lista
            }
    }

    fun cambiarVisibilidad(id: String, nuevoEstado: Boolean) {
        firestore.collection("products")
            .document(id)
            .update("visible", nuevoEstado)
            .addOnSuccessListener {
                cargarProductos() // recargar lista
            }
    }

    fun eliminarProducto(id: String) {
        firestore.collection("products")
            .document(id)
            .delete()
            .addOnSuccessListener {
                cargarProductos() // recargar lista
            }
    }
}

