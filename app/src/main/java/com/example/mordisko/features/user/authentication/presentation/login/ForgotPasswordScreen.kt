package com.example.mordisko.features.user.authentication.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onSendRecoveryEmail() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Enviando..." else "Enviar correo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin) {
                Text("Volver al inicio de sesión")
            }
        }
    }

    // Ejemplo de mensaje con Snackbar
    if (message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            },
            title = { Text("Correo enviado") },
            text = { Text(message ?: "") }
        )
    }
}