package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy // 🟠 icono copiar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager // 🟠 clipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.widget.Toast

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

    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    // 🟠 utilidades para copiar
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    fun copy(text: String, toast: String) {
        clipboard.setText(AnnotatedString(text))
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        viewModel.monitorPaymentStatus(orderNumber)
        viewModel.cargarPagoMovil()
    }

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
                title = {
                    Text(
                        "Realiza el Pago",
                        color = orange,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = orange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = lightOrange),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Monto
                    Text(
                        text = "💳 Monto a pagar:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val montoFormateado = String.format("%.2f", montoTotal)

                        Text(
                            text = "Bs $montoFormateado",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            copy("Bs $montoFormateado", "Monto copiado")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copiar monto",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🏦 Banco
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏦 Banco: ${pagoMovilData.banco}",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            copy(pagoMovilData.banco, "Banco copiado")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copiar banco",
                                tint = Color.White
                            )
                        }
                    }

                    // 📲 Teléfono
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📲 Teléfono: ${pagoMovilData.telefono}",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            copy(pagoMovilData.telefono, "Teléfono copiado")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copiar teléfono",
                                tint = Color.White
                            )
                        }
                    }

                    // 🆔 CI/RIF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🆔 CI: ${pagoMovilData.cedula}",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            copy(pagoMovilData.cedula, "CI copiado")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copiar CI",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⏳ Estado actual: ${paymentStatus.uppercase()}",
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🟠 Copiar TODOS los datos (centrado con icono)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val todo = buildString {
                                appendLine("Banco: ${pagoMovilData.banco}")
                                appendLine("Teléfono: ${pagoMovilData.telefono}")
                                appendLine("CI: ${pagoMovilData.cedula}")
                                append("Monto: Bs $montoTotal")
                            }
                            copy(todo, "Datos de pago copiados")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copiar todos los datos",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Copiar datos",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onComprobarPago,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orange)
            ) {
                Text("Comprobar Pago", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("Cancelar Pedido", fontWeight = FontWeight.Bold)
            }
        }
    }
}