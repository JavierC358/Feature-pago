package com.example.mordisko.features.admin.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.admin.categories.domain.CategoriesRepository
import com.example.mordisko.features.admin.categories.domain.Category
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val repo: CategoriesRepository
) : ViewModel() {

    private var reg: ListenerRegistration? = null

    private val _items = MutableStateFlow<List<Category>>(emptyList())
    val items: StateFlow<List<Category>> = _items

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        observeAll()
    }

    private fun observeAll() {
        _loading.value = true
        reg?.remove()
        reg = repo.listenAllOrdered { list ->
            _items.value = list
            _loading.value = false
        }
    }

    fun createCategory(name: String) = viewModelScope.launch {
        try {
            val nextPos = (_items.value.maxOfOrNull { it.position } ?: 0) + 1
            repo.create(name = name, position = nextPos)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun updateName(id: String, name: String) = viewModelScope.launch {
        try { repo.updateName(id, name) } catch (e: Exception) { _error.value = e.message }
    }

    fun toggleVisible(id: String, visible: Boolean) = viewModelScope.launch {
        try { repo.updateVisibility(id, visible) } catch (e: Exception) { _error.value = e.message }
    }

    fun updateImageUrl(id: String, imageUrl: String) = viewModelScope.launch {
        try { repo.updateImageUrl(id, imageUrl) } catch (e: Exception) { _error.value = e.message }
    }

    fun moveUp(index: Int) = reorder(index, index - 1)
    fun moveDown(index: Int) = reorder(index, index + 1)

    private fun reorder(from: Int, to: Int) = viewModelScope.launch {
        val list = _items.value.toMutableList()
        if (from in list.indices && to in list.indices) {
            val item = list.removeAt(from)
            list.add(to, item)
            repo.updatePositions(list.map { it.id })
        }
    }

    override fun onCleared() {
        reg?.remove()
        super.onCleared()
    }
}