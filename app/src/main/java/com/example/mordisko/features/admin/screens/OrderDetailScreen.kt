package com.example.mordisko.features.admin.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.features.user.cart.domain.model.CartItem
import com.example.mordisko.features.admin.viewmodel.OrderDetailViewModel
import com.example.mordisko.features.user.cart.domain.model.OrderModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderNumber: String,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val orderState by viewModel.order.collectAsState()

    LaunchedEffect(orderNumber) {
        viewModel.loadOrder(orderNumber)
    }

    // Bloquear retroceso
    BackHandler(enabled = true) {}

    if (orderState == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cargando pedido...") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    val order = orderState!!
    val googleMapsUrl = remember(order.address) {
        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(order.address)}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                // 🧾 Datos generales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🧾 Orden: ${order.orderNumber}", fontWeight = FontWeight.Bold)
                        Text("🕒 ${order.timestamp?.toDate()}")
                        Text("🧍 Usuario: ${order.userId}")
                        Text("💳 Método de pago: ${order.paymentMethod}")
                        Text("📦 Entrega: ${order.deliveryOption}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📍 Dirección
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📍 Dirección de entrega:", fontWeight = FontWeight.Bold)
                        Text(order.address)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, googleMapsUrl)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir dirección"))
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Compartir enlace")
                            }

                            if (order.deliveryOption.lowercase() == "moto") {
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Icon(Icons.Default.Map, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Ver en mapa")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🧾 Productos
                Text("🧾 Productos:", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }

            items(order.items) { item: CartItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🍕 ${item.name} (${item.size})", fontWeight = FontWeight.SemiBold)
                        Text("Cantidad: ${item.quantity}")
                        Text("Precio: $${item.priceUsd}")

                        if (item.extras.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🧀 Extras:", fontWeight = FontWeight.Bold)
                            item.extras.forEach { extra ->
                                Text("- ${extra.name}: $${extra.priceUsd}")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // 💰 Totales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💰 Subtotal: $${order.subtotalUsd}")
                        Text("🚚 Delivery: $${order.deliveryCostUsd}")
                        Text("💵 Total (USD): $${order.totalUsd}", fontWeight = FontWeight.Bold)
                        Text("💱 Tasa de cambio: Bs ${order.exchangeRate}")
                        Text("💴 Total (Bs): Bs ${order.totalBs}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}