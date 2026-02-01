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
import androidx.compose.runtime.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.UUID
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryOption

@Composable
fun OrderSummaryScreen(
    cartViewModel: CartViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.collectAsState()
    val deliveryOption by cartViewModel.deliveryOption.collectAsState()
    val address by cartViewModel.secondaryAddress.collectAsState()
    val reference by cartViewModel.addressReference.collectAsState()
    val paymentMethod by cartViewModel.paymentMethod.collectAsState()
    val exchangeRate by cartViewModel.exchangeRate.collectAsState()
    val deliveryCostUsd by cartViewModel.deliveryCost.collectAsState()
    val isPlacingOrder by cartViewModel.isPlacingOrder.collectAsState()

    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    LaunchedEffect(Unit) {
        cartViewModel.cargarExchangeRateDesdeFirestore()
        if (exchangeRate == 0.0) {
            cartViewModel.loadExchangeRate()
        }

        // ✅ Aseguramos costo mínimo si la opción es Moto
        if (deliveryOption == DeliveryOption.Moto && deliveryCostUsd == 0.0) {
            cartViewModel.setDeliveryCost(2.0)
        }
    }

    val subtotalUsd = cartItems.sumOf { it.priceUsd * it.quantity }
    val totalUsd = subtotalUsd + deliveryCostUsd
    val totalBs = totalUsd * exchangeRate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = orange)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", fontSize = 16.sp, color = orange)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumen del pedido",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = orange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de productos
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightOrange)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                cartItems.forEach { item ->
                    Text("- ${item.name}: ${item.quantity} x $${item.priceUsd}", color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))

                val orderComment by cartViewModel.orderComment.collectAsState()
                if (orderComment.isNotBlank()) {
                    Text("📝 Nota del cliente:", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(orderComment, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Divider(color = Color.White.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Subtotal: $${"%.2f".format(subtotalUsd)}", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de envío
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightOrange)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Método de entrega: ${deliveryOption?.name ?: "No seleccionado"}", color = Color.White)

                if (deliveryOption == DeliveryOption.Moto) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Costo de entrega: $${"%.2f".format(deliveryCostUsd)}", color = Color.White)
                    Text("Dirección: $address", color = Color.White)
                    if (reference.isNotBlank()) {
                        Text("Referencia: $reference", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de totales
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightOrange)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Tasa de cambio: Bs/\$. $exchangeRate", color = Color.White)
                Text("Total en USD: $${"%.2f".format(totalUsd)}", color = Color.White)
                Text("Total en Bs: Bs. ${"%.2f".format(totalBs)}", fontWeight = FontWeight.Bold, color = Color.White)

                Spacer(modifier = Modifier.height(8.dp))

                val metodoPagoTexto = when (paymentMethod) {
                    PaymentMethod.PagoMovil -> "Pago móvil"
                    PaymentMethod.PuntoDeVenta -> "Punto de venta"
                    PaymentMethod.Efectivo -> "Efectivo"
                    else -> "No seleccionado"
                }

                Text("Método de pago: $metodoPagoTexto", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

// requestId por intento de checkout (luego lo haremos real en Firestore)
        val requestId = rememberSaveable { UUID.randomUUID().toString() }

// Anti doble navegación
        var hasNavigated by rememberSaveable { mutableStateOf(false) }

        Button(
            onClick = {
                if (isPlacingOrder) return@Button

                cartViewModel.placeOrder(
                    exchangeRate = exchangeRate,
                    deliveryCostUsd = deliveryCostUsd,
                    clearCartOnSuccess = true,
                    requestId = requestId, // ✅ CLAVE: idempotencia real
                    onResult = { success, error, orderNumber ->
                        if (success && orderNumber != null) {

                            // ✅ Evita doble navegación si llega doble callback
                            if (hasNavigated) return@placeOrder
                            hasNavigated = true

                            val encodedOrder = URLEncoder.encode(orderNumber, StandardCharsets.UTF_8.toString())
                            when (paymentMethod) {
                                PaymentMethod.PagoMovil -> {
                                    navController.navigate("order_status/$encodedOrder/${"%.2f".format(totalBs)}")
                                }
                                PaymentMethod.PuntoDeVenta, PaymentMethod.Efectivo -> {
                                    navController.navigate("pedido_en_proceso/$encodedOrder")
                                }
                                else -> {
                                    hasNavigated = false
                                    Toast.makeText(context, "Método de pago no válido", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            },
            enabled = !isPlacingOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orange)
        ) {
            Text(
                text = if (isPlacingOrder) "Enviando..." else "Confirmar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}