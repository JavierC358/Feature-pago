package com.example.mordisko.features.admin.data

import android.content.Context
import android.util.Log
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.google.firebase.firestore.FirebaseFirestore

fun subirProductosAFirestore(context: Context, productos: List<PizzaItem>) {
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("products")

    for (item in productos) {
        val data = hashMapOf(
            "name" to item.name,
            "description" to item.description,
            "imageUrl" to item.imageUrl,
            "category" to item.category.name,
            "priceBySize" to item.priceBySize,
            "priceUsd" to item.priceUsd
        )

        collection.document(item.name).set(data)
            .addOnSuccessListener {
                Log.d("UPLOAD", "✅ ${item.name} subido o actualizado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("UPLOAD", "❌ Error al subir ${item.name}", e)
            }
    }
}
