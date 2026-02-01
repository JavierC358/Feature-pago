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
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryOption
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.alpha

enum class PaymentMethod {
    PagoMovil, Efectivo, PuntoDeVenta
}

@Composable
fun PaymentMethodScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onContinue: (PaymentMethod) -> Unit // ✅ ahora es un callback correcto
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)
    val deliveryOption by cartViewModel.deliveryOption.collectAsState()
    val onlyPagoMovil = deliveryOption == DeliveryOption.Moto

    LaunchedEffect(onlyPagoMovil) {
        if (onlyPagoMovil) {
            selectedMethod = PaymentMethod.PagoMovil
            cartViewModel.setPaymentMethod(PaymentMethod.PagoMovil)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
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
                text = "Selecciona el método de pago",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = orange,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (onlyPagoMovil) {
                Text(
                    text = "Para entrega en moto, solo está disponible Pago Móvil.",
                    color = orange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            PaymentOptionCard(
                icon = Icons.Default.PhoneIphone,
                title = "Pago Móvil",
                isSelected = selectedMethod == PaymentMethod.PagoMovil,
                enabled = true,
                onClick = { selectedMethod = PaymentMethod.PagoMovil },
                highlightColor = lightOrange
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentOptionCard(
                icon = Icons.Default.CreditCard,
                title = "Punto de Venta",
                isSelected = selectedMethod == PaymentMethod.PuntoDeVenta,
                enabled = !onlyPagoMovil,
                onClick = { selectedMethod = PaymentMethod.PuntoDeVenta },
                highlightColor = lightOrange
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentOptionCard(
                icon = Icons.Default.Money,
                title = "Efectivo",
                isSelected = selectedMethod == PaymentMethod.Efectivo,
                enabled = !onlyPagoMovil,
                onClick = { selectedMethod = PaymentMethod.Efectivo },
                highlightColor = lightOrange
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    selectedMethod?.let {
                        cartViewModel.setPaymentMethod(it)
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(50)
                            onContinue(it) // ✅ se ejecuta el callback enviado
                        }
                    }
                },
                enabled = selectedMethod != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orange)
            ) {
                Text(
                    "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PaymentOptionCard(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    highlightColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
) {
    val alpha = if (enabled) 1f else 0.45f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) highlightColor else Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .alpha(alpha)
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFFE05B13))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}
