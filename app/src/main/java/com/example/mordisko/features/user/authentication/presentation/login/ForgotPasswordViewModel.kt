package com.example.mordisko.features.user.authentication.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.authentication.login.domain.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onSendRecoveryEmail() {
        val currentEmail = email.value.trim()
        Log.d("ForgotPassword", "Intentando enviar recuperación a: $currentEmail")

        if (currentEmail.isEmpty()) {
            _message.value = "Por favor, introduce un correo electrónico."
            Log.w("ForgotPassword", "✖ Email vacío")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = forgotPasswordUseCase.sendPasswordReset(currentEmail)
            _isLoading.value = false

            result.onSuccess {
                Log.d("ForgotPassword", "Correo enviado con éxito.")
                _message.value = "Hemos enviado un correo para restablecer tu contraseña. Por favor revisa tu bandeja de entrada y haz clic en el enlace para continuar."
            }.onFailure {
                Log.e("ForgotPassword", "Fallo al enviar correo: ${it.localizedMessage}", it)
                _message.value = "Error al enviar el correo: ${it.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}