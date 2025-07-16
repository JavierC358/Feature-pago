package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    orderNumber: String,
    montoTotal: Double,
    onComprobarPago: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: OrderStatusViewModel = hiltViewModel(),
    navController: NavController
) {
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    val pagoMovilData = viewModel.pagoMovilData.value

    // Escuchar estado del pago
    LaunchedEffect(Unit) {
        viewModel.monitorPaymentStatus(orderNumber)
        viewModel.cargarPagoMovil() // ✅ Cargar datos al entrar
    }

    // Navegar si fue verificado
    LaunchedEffect(paymentStatus) {
        if (paymentStatus == "verificado") {
            navController.navigate("pedido_verificado") {
                popUpTo("order_status/$orderNumber/$montoTotal") { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado del Pago") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Orden N°: $orderNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "💳 Monto a pagar:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Bs $montoTotal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "🏦 Banco: ${pagoMovilData.banco}\n" +
                                "📲 Pago móvil: ${pagoMovilData.telefono}\n" +
                                "🆔 CI/RIF: ${pagoMovilData.cedula}",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "⏳ Estado actual: ${paymentStatus.uppercase()}",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onComprobarPago,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Comprobar Pago")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("Cancelar Pedido")
            }
        }
    }
}