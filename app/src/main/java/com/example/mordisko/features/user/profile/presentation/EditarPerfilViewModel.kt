package com.example.mordisko.features.user.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.profile.domain.model.UserProfile
import com.example.mordisko.features.user.profile.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarPerfilViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Campos de error individuales
    private val _nombreError = MutableStateFlow<String?>(null)
    val nombreError: StateFlow<String?> = _nombreError

    private val _correoError = MutableStateFlow<String?>(null)
    val correoError: StateFlow<String?> = _correoError

    private val _telefonoError = MutableStateFlow<String?>(null)
    val telefonoError: StateFlow<String?> = _telefonoError

    private val _cedulaError = MutableStateFlow<String?>(null)
    val cedulaError: StateFlow<String?> = _cedulaError

    fun onImageSelected(uri: Uri) {
        _imageUri.value = uri
    }

    fun onProfileChange(updated: UserProfile) {
        _profile.value = updated
        clearFieldErrors()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val result = userRepository.getUserProfile()
            result.onSuccess {
                _profile.value = it
            }
        }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        if (!validateFields()) return

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            val result = userRepository.saveUserProfile(_profile.value, _imageUri.value)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message
            }

            _isSaving.value = false
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val p = _profile.value

        if (p.nombre.isBlank()) {
            _nombreError.value = "El nombre es requerido"
            isValid = false
        }

        if (p.correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(p.correo).matches()) {
            _correoError.value = "Correo inválido"
            isValid = false
        }

        if (p.telefono.isBlank() || p.telefono.length !in 10..11) {
            _telefonoError.value = "Teléfono inválido"
            isValid = false
        }

        if (p.cedula.isBlank() || !p.cedula.all { it.isDigit() }) {
            _cedulaError.value = "Cédula inválida"
            isValid = false
        }

        return isValid
    }

    private fun clearFieldErrors() {
        _nombreError.value = null
        _correoError.value = null
        _telefonoError.value = null
        _cedulaError.value = null
    }
}