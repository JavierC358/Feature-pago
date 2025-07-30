package com.example.mordisko.features.user.history.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.history.presentation.viewmodel.OrderHistoryViewModel
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: OrderHistoryViewModel,
    cartViewModel: CartViewModel,
    onRepetirPedido: () -> Unit,
    onBack: () -> Unit
) {
    val pedidos by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(color = orange)
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }

                pedidos.isEmpty() -> {
                    Text(
                        text = "No tienes pedidos registrados.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        items(pedidos) { pedido ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = lightOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Orden #${pedido.orderNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verificado",
                                            tint = if (pedido.paymentStatus == "verificado") Color.Green else Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Fecha: ${pedido.formattedDate()}")
                                    Text("Total: ${pedido.totalUsd} USD / ${pedido.totalBs} Bs")
                                    Text("Método de pago: ${pedido.paymentMethod}")
                                    Text("Estado: ${pedido.paymentStatus}")

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            cartViewModel.repetirPedido(pedido.items)
                                            onRepetirPedido()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = orange)
                                    ) {
                                        Text("Repetir pedido", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}