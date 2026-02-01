package com.example.mordisko.features.user.authentication.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.authentication.login.domain.LoginUseCase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoginEnable = MutableStateFlow(false)
    val isLoginEnable: StateFlow<Boolean> = _isLoginEnable

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _userRole = MutableStateFlow("")
    val userRole: StateFlow<String> = _userRole

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _isLoginEnable.value = enableLogin(email, password)

        // ✅ si el usuario está corrigiendo, quitamos el mensaje anterior
        _loginError.value = null
    }

    private fun enableLogin(email: String, password: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
    }

    fun onLoginSelected(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)

            if (result.isSuccess) {
                _loginError.value = null

                val role = loginUseCase.getUserRole()
                _userRole.value = role ?: "cliente"

                _loginSuccess.value = true
                onSuccess()
            } else {
                val e = result.exceptionOrNull()

                // ✅ Si quieres los 5 mensajes “bonitos”, los usas como fallback
                val messages = listOf(
                    "Ups… correo o contraseña incorrectos. Inténtalo de nuevo.",
                    "No pudimos iniciar sesión. Revisa tu correo y tu contraseña.",
                    "Datos inválidos. Asegúrate de escribirlos correctamente.",
                    "Correo/contraseña incorrectos. Intenta nuevamente, por favor.",
                    "Inicio de sesión fallido. Verifica los datos e intenta otra vez."
                )

                val fallback = messages[(System.currentTimeMillis() % messages.size).toInt()]
                _loginError.value = mapLoginError(e).ifBlank { fallback }

                _loginSuccess.value = false
            }
        }
    }

    private fun mapLoginError(e: Throwable?): String {
        return when (e) {
            is FirebaseNetworkException ->
                "No pudimos conectarnos. Revisa tu internet e inténtalo nuevamente."

            is FirebaseTooManyRequestsException ->
                "Demasiados intentos. Espera un momento e inténtalo otra vez."

            is FirebaseAuthInvalidUserException ->
                "Este correo no está registrado. Crea una cuenta o verifica el correo."

            is FirebaseAuthInvalidCredentialsException -> {
                // Puede ser formato de correo inválido o contraseña incorrecta.
                // Firebase a veces trae un errorCode útil:
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" ->
                        "El correo no tiene un formato válido. Ejemplo: correo@dominio.com"
                    else ->
                        "Correo o contraseña incorrectos. Por favor verifica tus datos e inténtalo de nuevo."
                }
            }

            else ->
                "Correo o contraseña incorrectos. Por favor verifica tus datos e inténtalo de nuevo."
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }
}