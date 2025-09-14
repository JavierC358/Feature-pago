package com.example.mordisko.features.user.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.history.data.OrdersRepository
import com.example.mordisko.features.user.history.domain.model.OrderHistoryItem
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val repository: OrdersRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderHistoryItem>>(emptyList())
    val orders: StateFlow<List<OrderHistoryItem>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // UI de paginación
    private val _pageSize = MutableStateFlow(8) // default “Reg…”
    val pageSize: StateFlow<Int> = _pageSize

    private val _currentPage = MutableStateFlow(1) // 1-based
    val currentPage: StateFlow<Int> = _currentPage

    private val _totalCount = MutableStateFlow(0L)
    val totalCount: StateFlow<Long> = _totalCount

    val totalPages: StateFlow<Int> = combine(_totalCount, _pageSize) { count, size ->
        if (size <= 0) 1 else max(1, ceil(count.toDouble() / size).toInt())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    // Cursores por página: index 1 -> cursor de la página 1 (null), 2 -> lastDoc de la página 1, etc.
    private val pageCursors: MutableList<DocumentSnapshot?> = mutableListOf(null)

    init {
        viewModelScope.launch {
            _totalCount.value = repository.getOrdersCount()
            loadPage(1)
        }
    }

    fun onChangePageSize(newSize: Int) {
        if (newSize <= 0 || _pageSize.value == newSize) return
        _pageSize.value = newSize
        // Reiniciamos
        _currentPage.value = 1
        pageCursors.clear()
        pageCursors.add(null)
        viewModelScope.launch {
            _totalCount.value = repository.getOrdersCount()
            loadPage(1)
        }
    }

    fun nextPage() {
        val next = _currentPage.value + 1
        val maxPages = totalPages.value
        if (next > maxPages) return
        loadPage(next)
    }

    fun prevPage() {
        val prev = _currentPage.value - 1
        if (prev < 1) return
        loadPage(prev)
    }

    private fun loadPage(page: Int) {
        if (_isLoading.value) return
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val size = _pageSize.value.toLong()

                // Asegura que tenemos cursor para esta página
                // cursor para página N = lastDoc de la página N-1
                val cursorForPage = if (page - 1 < pageCursors.size) {
                    pageCursors[page - 1]
                } else {
                    // Si piden saltar varias páginas, avanzamos iterando
                    var c: DocumentSnapshot? = pageCursors.last()
                    var p = pageCursors.size // página ya cacheada
                    while (p < page) {
                        val (_, last) = repository.getOrdersPage(size, c)
                        pageCursors.add(last) // cursor para la página siguiente
                        c = last
                        p++
                        if (last == null) break
                    }
                    pageCursors.getOrNull(page - 1)
                }

                val (list, last) = repository.getOrdersPage(size, cursorForPage)
                _orders.value = list
                _currentPage.value = page

                // Guarda cursor para la página siguiente si hace falta
                if (pageCursors.size <= page) {
                    pageCursors.add(last)
                } else {
                    pageCursors[page] = last
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al paginar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _totalCount.value = repository.getOrdersCount()
            // Resetea a la página 1 conservando pageSize
            _currentPage.value = 1
            pageCursors.clear()
            pageCursors.add(null)
            loadPage(1)
        }
    }
}