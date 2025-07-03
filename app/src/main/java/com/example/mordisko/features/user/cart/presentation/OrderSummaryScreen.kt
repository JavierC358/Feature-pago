package com.example.mordisko.features.user.cart.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun OrderSummaryScreen(
    cartViewModel: CartViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartItems = cartViewModel.cartItems.collectAsState().value
    val deliveryOption = cartViewModel.deliveryOption.collectAsState().value
    val address = cartViewModel.secondaryAddress.collectAsState().value
    val reference = cartViewModel.addressReference.collectAsState().value
    val paymentMethod = cartViewModel.paymentMethod.collectAsState().value
    val exchangeRate = 100.0

    val deliveryCostUsd = when (deliveryOption) {
        DeliveryOption.Moto -> 2.0
        else -> 0.0
    }

    val subtotalUsd = cartItems.sumOf { it.priceUsd * it.quantity }
    val totalUsd = subtotalUsd + deliveryCostUsd
    val totalBs = totalUsd * exchangeRate

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔙 Botón Volver
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver", fontSize = 16.sp)
            }

            // 🧾 Título
            Text(
                text = "Resumen del pedido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 🧾 Productos
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        Text("- ${item.name}: ${item.quantity} x $${item.priceUsd}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Subtotal: $${"%.2f".format(subtotalUsd)}", fontWeight = FontWeight.Bold)
                }
            }

            // 🛵 Método de entrega
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Método de entrega: ${deliveryOption?.name ?: "No seleccionado"}")
                    if (deliveryCostUsd > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Costo de entrega: $${"%.2f".format(deliveryCostUsd)}")
                        Text("Dirección: $address")
                        if (reference.isNotBlank()) Text("Referencia: $reference")
                    }
                }
            }

            // 💲 Totales y método de pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tasa de cambio: Bs/$. $exchangeRate")
                    Text("Total en USD: $${"%.2f".format(totalUsd)}")
                    Text("Total en Bs: Bs. ${"%.2f".format(totalBs)}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val metodoPagoTexto = when (paymentMethod) {
                        PaymentMethod.PagoMovil -> "Pago móvil"
                        PaymentMethod.PuntoDeVenta -> "Punto de venta"
                        PaymentMethod.Efectivo -> "Efectivo"
                        else -> "No seleccionado"
                    }

                    Text("Método de pago: $metodoPagoTexto")
                }
            }

            // ✅ Confirmar
            Button(
                onClick = {
                    cartViewModel.placeOrder(
                        exchangeRate = exchangeRate,
                        deliveryCostUsd = deliveryCostUsd,
                        onResult = { success, error, orderNumber ->
                            if (success && orderNumber != null) {
                                val encodedOrder = URLEncoder.encode(orderNumber, StandardCharsets.UTF_8.toString())
                                navController.navigate("order_status/$encodedOrder/${"%.2f".format(totalBs)}")
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar")
            }
        }
    }
}