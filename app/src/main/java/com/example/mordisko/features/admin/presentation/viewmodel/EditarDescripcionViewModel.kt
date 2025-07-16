package com.example.mordisko.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProductoConDescripcion(
    val id: String,
    val name: String,
    val descripcion: String?
)

class EditarDescripcionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _productos = MutableStateFlow<List<ProductoConDescripcion>>(emptyList())
    val productos: StateFlow<List<ProductoConDescripcion>> = _productos

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("products").get().await()
                val lista = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val descripcion = doc.getString("descripcion")
                    ProductoConDescripcion(id, name, descripcion)
                }
                _productos.value = lista
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun actualizarDescripcion(productId: String, nuevaDescripcion: String) {
        viewModelScope.launch {
            try {
                db.collection("products")
                    .document(productId)
                    .update("descripcion", nuevaDescripcion)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}