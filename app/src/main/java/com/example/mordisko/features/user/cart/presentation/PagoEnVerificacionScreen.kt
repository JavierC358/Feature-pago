package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun PagoEnVerificacionScreen(
    orderNumber: String,
    navController: NavController,
    viewModel: OrderStatusViewModel = hiltViewModel()
) {
    val paymentStatus by viewModel.paymentStatus.collectAsState(initial = "por_verificar")

    LaunchedEffect(Unit) {
        viewModel.monitorPaymentStatus(orderNumber)
    }

    LaunchedEffect(paymentStatus) {
        if (paymentStatus.equals("verificado", ignoreCase = true)) {
            navController.navigate("pedido_verificado") {
                popUpTo("payment_pending/$orderNumber") { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Estamos verificando tu pago…")
                Spacer(Modifier.height(8.dp))
                Text("Orden N°: $orderNumber", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}