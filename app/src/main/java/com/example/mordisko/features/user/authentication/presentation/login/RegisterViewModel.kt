package com.example.mordisko.features.user.authentication.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mordisko.features.user.authentication.login.domain.repository.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isRegisterEnabled = MutableStateFlow(false)
    val isRegisterEnabled: StateFlow<Boolean> = _isRegisterEnabled

    fun onRegisterChanged(email: String, password: String, confirmPassword: String) {
        _email.value = email
        _password.value = password
        _confirmPassword.value = confirmPassword
        _isRegisterEnabled.value = validateInputs(email, password, confirmPassword)
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
                && password.length >= 6
                && confirmPassword == password
    }

    fun onRegisterSelected(onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val result = registerUseCase(email.value.trim(), password.value)

            if (result.isSuccess) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
}