package com.example.mordisko.features.user.support

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class SupportConfig(
    val whatsappNumber: String = "",
    val defaultMessage: String = ""
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _supportConfig = MutableStateFlow(SupportConfig())
    val supportConfig: StateFlow<SupportConfig> = _supportConfig

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadSupportConfig()
    }

    private fun loadSupportConfig() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("config")
                    .document("support")
                    .get()
                    .await()

                val number = snapshot.getString("whatsappNumber") ?: ""
                val message = snapshot.getString("defaultMessage") ?: ""

                _supportConfig.value = SupportConfig(number, message)
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando soporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para construir el enlace de WhatsApp
    fun buildWhatsAppLink(): String {
        val number = _supportConfig.value.whatsappNumber
        val message = _supportConfig.value.defaultMessage
        return if (number.isNotEmpty()) {
            "https://wa.me/${number.replace("+", "").replace(" ", "")}?text=${Uri.encode(message)}"
        } else {
            ""
        }
    }
}
