package com.example.mordisko.features.user.authentication.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.authentication.login.domain.LoginUseCase
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
    }

    private fun enableLogin(email: String, password: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
    }

    fun onLoginSelected(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            if (result) {
                _loginSuccess.value = true
                _loginError.value = null

                val role = loginUseCase.getUserRole()
                _userRole.value = role ?: "cliente"

                onSuccess()
            } else {
                _loginError.value = "Correo o contraseña incorrectos"
                _loginSuccess.value = false
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }
}