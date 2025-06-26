package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.user.orders.presentation.PaymentVerificationViewModel
import kotlinx.coroutines.launch

@Composable
fun VerificarPagoScreen(
    orderNumber: String,
    onCerrar: () -> Unit,
    viewModel: PaymentVerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val orange = Color(0xFFE05B13)

    var monto by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Mostrar Snackbar de éxito
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("¡Verificación enviada con éxito!")
            viewModel.resetState()
            onCerrar()
        }
    }

    // Mostrar Snackbar de error
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Error: $it")
                viewModel.resetState()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Suministra la información exacta de tu pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto pagado (Bs.)") },
                    placeholder = { Text("Ej: 109.50") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = referencia,
                    onValueChange = { referencia = it },
                    label = { Text("Últimos 4 dígitos de la referencia") },
                    placeholder = { Text("Ej: 4582") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono desde donde pagaste") },
                    placeholder = { Text("Ej: 04141234567") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCerrar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar")
                    }

                    Button(
                        onClick = {
                            if (monto.isNotBlank() && referencia.length == 4 && telefono.length >= 11) {
                                viewModel.submitPaymentVerification(
                                    orderNumber,
                                    monto,
                                    referencia,
                                    telefono
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = orange),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        Text(if (state.isLoading) "Enviando..." else "Verificar")
                    }
                }
            }
        }
    }
}
