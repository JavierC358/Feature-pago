package com.example.mordisko.features.help.faqs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.help.faqs.domain.FaqsRepository
import com.example.mordisko.features.help.faqs.domain.model.Faq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaqsViewModel @Inject constructor(
    private val repo: FaqsRepository
) : ViewModel() {

    private val _faqs = MutableStateFlow<List<Faq>>(emptyList())
    val faqs: StateFlow<List<Faq>> = _faqs

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            repo.observeFaqs()
                .onStart {
                    _loading.value = true
                    _error.value = null
                }
                .catch { e ->
                    _error.value = e.message ?: "No se pudieron cargar las preguntas."
                    _faqs.value = emptyList()
                }
                .collect { list ->
                    _faqs.value = list
                    _loading.value = false
                }
        }
    }
}