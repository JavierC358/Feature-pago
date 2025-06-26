package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderStatusScreen(
    orderNumber: String?,
    montoTotal: Double, // 👈 nuevo parámetro
    onComprobarPago: () -> Unit,
    onCancelar: () -> Unit
) {
    val orange = Color(0xFFE05B13)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Pedido confirmado",
                tint = orange,
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "¡Gracias por tu compra!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Realiza el pago móvil por el monto exacto y luego presiona 'Comprobar' para verificar tu pago.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Color.Gray
            )

            Text(
                text = "Monto a transferir: Bs. %.2f".format(montoTotal),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Datos del Pago Móvil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Banco: Banco Mercantil", fontSize = 16.sp)
                Text("Teléfono: 0414-1234567", fontSize = 16.sp)
                Text("Cédula/RIF: V-12345678", fontSize = 16.sp)
            }

            orderNumber?.let {
                Text(
                    text = "Número de orden: $it",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onComprobarPago,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange)
                ) {
                    Text("Comprobar")
                }

                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
