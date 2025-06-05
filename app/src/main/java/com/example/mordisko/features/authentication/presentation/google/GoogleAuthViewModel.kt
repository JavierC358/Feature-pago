package com.example.mordisko.features.authentication.presentation.google

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.authentication.login.domain.GoogleSignInUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError


    fun handleGoogleSignInResult(data: Intent?, activity: Activity) {
        Log.d("GoogleLogin", "Intent recibido, intentando obtener cuenta de Google")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val account = data?.let { googleSignInUseCase.getSignedInAccountFromIntent(it) }

                if (account != null) {
                    Log.d("GoogleLogin", "Cuenta obtenida correctamente: ${account.email}")
                    val result = googleSignInUseCase.signInWithGoogle(account, activity)
                    result.onSuccess {
                        Log.d("GoogleLogin", "Login con Google exitoso")
                        _isSuccess.value = true
                        _errorMessage.value = null
                    }.onFailure { exception ->
                        Log.e("GoogleLogin", "Fallo al autenticar con Google: ${exception.message}")
                        _isSuccess.value = false // 👈 Agregado para prevenir navegación
                        _errorMessage.value = "Error al iniciar sesión con Google: ${exception.localizedMessage}"
                    }
                } else {
                    _isSuccess.value = false // 👈 También agregado aquí
                    _errorMessage.value = "No se pudo obtener la cuenta de Google."
                }

            } catch (e: Exception) {
                _isSuccess.value = false // 👈 También en caso de error inesperado
                _errorMessage.value = "Se produjo un error inesperado: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun clearError() {
        _authError.value = null
    }

    fun resetState() {
        _isLoading.value = false
        _isSuccess.value = false
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _isSuccess.value = false
    }

    fun getSignInIntent(context: Context): Intent {
        return googleSignInUseCase.getSignInIntent(context)
    }

    fun getAccountFromIntent(data: Intent): GoogleSignInAccount? {
        return googleSignInUseCase.getSignedInAccountFromIntent(data)
    }

    fun setError(message: String) {
        _authError.value = message
    }
}