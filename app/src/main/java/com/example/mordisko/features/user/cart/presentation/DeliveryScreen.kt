package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DeliveryScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    viewModel: DeliveryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val address by cartViewModel.secondaryAddress.collectAsState()
    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    LaunchedEffect(selectedOption) {
        selectedOption?.let {
            cartViewModel.setDeliveryOption(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
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
            text = "¿Cómo deseas recibir tu pedido?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = orange,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DeliveryOptionCard(
            icon = Icons.Default.DeliveryDining,
            title = "Moto",
            description = "Costo del servicio: 2 $",
            isSelected = selectedOption == DeliveryOption.Moto,
            content = {
                if (selectedOption == DeliveryOption.Moto) {
                    Column {
                        Text(
                            text = address.ifBlank { "No se ha seleccionado dirección aún" },
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (address.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val reference = cartViewModel.addressReference.collectAsState().value

                            OutlinedTextField(
                                value = reference,
                                onValueChange = {
                                    if (it.length <= 160) {
                                        cartViewModel.onAddressReferenceChanged(it)
                                    }
                                },
                                label = { Text("Referencia adicional") },
                                supportingText = {
                                    Text("${reference.length}/160")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                navController.navigate("map")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = orange),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Seleccionar dirección en el mapa", color = Color.White)
                        }
                    }
                }
            },
            onClick = { viewModel.onOptionSelected(DeliveryOption.Moto) },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeliveryOptionCard(
            icon = Icons.Default.LocalDining,
            title = "Consumo en tienda",
            isSelected = selectedOption == DeliveryOption.EnTienda,
            onClick = { viewModel.onOptionSelected(DeliveryOption.EnTienda) },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeliveryOptionCard(
            icon = Icons.Default.Store,
            title = "Retiro en tienda",
            isSelected = selectedOption == DeliveryOption.Retiro,
            onClick = { viewModel.onOptionSelected(DeliveryOption.Retiro) },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinue,
            enabled = selectedOption != null && (selectedOption != DeliveryOption.Moto || address.isNotBlank()),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DeliveryOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    isSelected: Boolean,
    content: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    highlightColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) highlightColor else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFFE05B13))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            }
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color.White)
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}
