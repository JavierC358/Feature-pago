package com.example.mordisko.features.user.authentication.presentation.login

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mordisko.R
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.features.user.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange



@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    googleAuthViewModel: GoogleAuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    navController: NavHostController,
    googleLauncher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoginEnabled by loginViewModel.isLoginEnable.collectAsState()
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()
    val userRole by loginViewModel.userRole.collectAsState()
    val loginError by loginViewModel.loginError.collectAsState()

    val isLoading by googleAuthViewModel.isLoading.collectAsState()
    val isSuccess by googleAuthViewModel.isSuccess.collectAsState()
    val authError by googleAuthViewModel.authError.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Éxito Google
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            googleAuthViewModel.clearSuccess()
            onLoginSuccess()
        }
    }

    // Éxito Email + Rol
    LaunchedEffect(loginSuccess, userRole) {
        if (loginSuccess && userRole.isNotBlank()) {
            when (userRole) {
                "admin" -> {
                    navController.navigate("elegir_rol") {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }

                "cliente" -> {
                    Log.d("LoginScreen", "Rol: cliente -> Navegar a HorarioScreen")
                    navController.navigate(Routes.Horario) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            }
            loginViewModel.clearLoginSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginError) {
        loginError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            loginViewModel.clearLoginError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Bienvenido a Sazón",
                    style = MaterialTheme.typography.headlineSmall,
                    color = orange
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { loginViewModel.onLoginChanged(it, password) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = orange,
                        unfocusedBorderColor = lightOrange,
                        focusedLabelColor = orange,
                        cursorColor = orange
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { loginViewModel.onLoginChanged(email, it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val desc =
                            if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = desc)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = orange,
                        unfocusedBorderColor = lightOrange,
                        focusedLabelColor = orange,
                        cursorColor = orange
                    )

                )

                Button(
                    onClick = {
                        loginViewModel.onLoginSelected(email, password, onSuccess = {})
                    },
                    enabled = isLoginEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orange,
                        contentColor = Color.White,
                        disabledContainerColor = orange.copy(alpha = 0.35f),
                        disabledContentColor = Color.White.copy(alpha = 0.8f)
                    )

                ) {
                    Text("Iniciar sesión")
                }

                TextButton(onClick = onNavigateToForgotPassword) {
                    Text("¿Olvidaste tu contraseña?")
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text("¿No tienes una cuenta? Regístrate")
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = lightOrange.copy(alpha = 0.6f)
                )

                Button(
                    onClick = {
                        val signInIntent = googleAuthViewModel.getSignInIntent(context)
                        googleLauncher.launch(signInIntent)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightOrange,
                        contentColor = Color.Black,
                        disabledContainerColor = lightOrange.copy(alpha = 0.5f),
                        disabledContentColor = Color.Black.copy(alpha = 0.7f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = orange,
                            trackColor = lightOrange.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciando con Google...")
                    } else {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Continuar con Google")
                    }
                }
            }

            authError?.let { error ->
                AlertDialog(
                    onDismissRequest = { googleAuthViewModel.clearError() },
                    title = { Text("Error de inicio de sesión") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(
                            onClick = { googleAuthViewModel.clearError() },
                            colors = ButtonDefaults.textButtonColors(contentColor = orange)
                        ) {
                            Text("Volver al login")
                        }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}