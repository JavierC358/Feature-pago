package com.example.mordisko.features.admin.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.admin.domain.AdminMenuRepository
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val repository: AdminMenuRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<PizzaItem>>(emptyList())
    val products: StateFlow<List<PizzaItem>> = _products

    fun getAllProductsFromFirestore() {
        viewModelScope.launch {
            try {
                val result = repository.getAllProducts()
                _products.value = result
                Log.d("AdminMenuViewModel", "🔥 Productos recibidos: ${result.size}")
            } catch (e: Exception) {
                _products.value = emptyList()
                Log.e("AdminMenuViewModel", "❌ Error al obtener productos: ${e.message}")
            }
        }
    }

    fun updateProductPrices(updatedProduct: PizzaItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateProduct(updatedProduct)
                onSuccess()
                getAllProductsFromFirestore()
            } catch (e: Exception) {
                // Manejo de error si lo deseas
            }
        }
    }

    fun uploadImageAndUpdateProduct(
        product: PizzaItem,
        imageUri: Uri,
        context: Context
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("product_images/${product.name.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Actualizar Firestore
                    FirebaseFirestore.getInstance()
                        .collection("products")
                        .document(product.id)
                        .update("imageUrl", downloadUrl.toString())
                        .addOnSuccessListener {
                            Log.d("ActualizarImagen", "✅ Imagen actualizada correctamente en Firestore")
                            getAllProductsFromFirestore() // Recarga la lista con la imagen nueva
                        }
                        .addOnFailureListener { e ->
                            Log.e("ActualizarImagen", "❌ Error al actualizar Firestore: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ActualizarImagen", "❌ Error al subir a Storage: ${e.message}")
            }
    }

    fun uploadBitmapAndUpdateProduct(product: PizzaItem, bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("product_images/${product.id}.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
                val imageData = baos.toByteArray()

                imageRef.putBytes(imageData).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()

                val updatedProduct = product.copy(imageUrl = downloadUrl)
                repository.updateProduct(updatedProduct)

                getAllProductsFromFirestore()

                Toast.makeText(context, "Imagen actualizada con éxito", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

}