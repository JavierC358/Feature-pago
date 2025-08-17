package com.example.mordisko.features.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.admin.categories.data.CategoryDto
import com.example.mordisko.features.admin.categories.domain.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeCategoriesViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private var reg: ListenerRegistration? = null

    init {
        reg = firestore.collection("categories")
            .whereEqualTo("visible", true)
            .orderBy("position")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    val d = doc.toObject(CategoryDto::class.java) ?: return@mapNotNull null
                    Category(
                        id = doc.id,
                        name = d.name,
                        imageUrl = d.imageUrl,
                        visible = d.visible,
                        position = d.position
                    )
                } ?: emptyList()
                _categories.value = list
                _loading.value = false
            }
    }

    override fun onCleared() {
        reg?.remove()
        super.onCleared()
    }
}