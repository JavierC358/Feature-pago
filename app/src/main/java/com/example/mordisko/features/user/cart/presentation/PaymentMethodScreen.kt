package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

enum class PaymentMethod {
    PagoMovil, Efectivo, PuntoDeVenta
}

@Composable
fun PaymentMethodScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onContinue: (PaymentMethod) -> Unit
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Botón volver
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

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Text(
                text = "Selecciona el método de pago",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Opciones de pago
            PaymentOptionCard(
                icon = Icons.Default.PhoneIphone,
                title = "Pago Móvil",
                isSelected = selectedMethod == PaymentMethod.PagoMovil,
                onClick = { selectedMethod = PaymentMethod.PagoMovil }
            )

            PaymentOptionCard(
                icon = Icons.Default.CreditCard,
                title = "Punto de Venta",
                isSelected = selectedMethod == PaymentMethod.PuntoDeVenta,
                onClick = { selectedMethod = PaymentMethod.PuntoDeVenta }
            )

            PaymentOptionCard(
                icon = Icons.Default.Money,
                title = "Efectivo",
                isSelected = selectedMethod == PaymentMethod.Efectivo,
                onClick = { selectedMethod = PaymentMethod.Efectivo }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedMethod?.let {
                        cartViewModel.setPaymentMethod(it)
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(50) // 50 ms o 1 frame
                            onContinue(it)
                        }
                    }
                },
                enabled = selectedMethod != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar")
            }
        }
    }
}

@Composable
fun PaymentOptionCard(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = title)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}