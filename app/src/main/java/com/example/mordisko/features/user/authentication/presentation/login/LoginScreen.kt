package com.example.mordisko.features.user.authentication.presentation.login

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.user.authentication.presentation.google.GoogleAuthViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    googleAuthViewModel: GoogleAuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    googleLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoginEnabled by loginViewModel.isLoginEnable.collectAsState()
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()

    val isLoading by googleAuthViewModel.isLoading.collectAsState()
    val isSuccess by googleAuthViewModel.isSuccess.collectAsState()
    val authError by googleAuthViewModel.authError.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Log.d("GoogleLogin", "Navegando al Home desde LoginScreen")
            googleAuthViewModel.clearSuccess()
            onLoginSuccess()
        }
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            Log.d("EmailLogin", "Navegando al Home desde LoginScreen (email)")
            loginViewModel.clearLoginSuccess()
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { loginViewModel.onLoginChanged(it, password) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { loginViewModel.onLoginChanged(email, it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    loginViewModel.onLoginSelected(email, password){
                        onLoginSuccess()
                    }
                },
                enabled = isLoginEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToForgotPassword) {
                Text("¿Olvidaste tu contraseña?")
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes una cuenta? Regístrate")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val signInIntent = googleAuthViewModel.getSignInIntent(context)
                    googleLauncher.launch(signInIntent)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciando con Google...")
                } else {
                    Text("Continuar con Google")
                }
            }
        }

        authError?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = { googleAuthViewModel.clearError() },
                title = { Text("Error de inicio de sesión") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { googleAuthViewModel.clearError() }) {
                        Text("Volver al login")
                    }
                }
            )
        }
    }
}