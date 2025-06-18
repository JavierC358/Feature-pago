package com.example.mordisko.features.cart.presentation

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
    val address by cartViewModel.secondaryAddress.collectAsState() // ✅ CAMBIO 1

    LaunchedEffect(selectedOption) {
        selectedOption?.let {
            cartViewModel.setDeliveryOption(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Escoge cómo recibirás tu pedido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            DeliveryOptionCard(
                icon = Icons.Default.DeliveryDining,
                title = "Moto",
                description = "Costo: 2 $",
                isSelected = selectedOption == DeliveryOption.Moto,
                content = {
                    if (selectedOption == DeliveryOption.Moto) {
                        Column {
                            Text(
                                text = address.ifBlank { "No se ha seleccionado dirección aún" },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // ✅ Campo para ingresar la referencia (solo si hay dirección)
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
                                    label = { Text("Anexa una referencia") },
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Seleccionar dirección en el mapa")
                            }
                        }
                    }
                },
                onClick = { viewModel.onOptionSelected(DeliveryOption.Moto) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DeliveryOptionCard(
                icon = Icons.Default.LocalDining,
                title = "Consumo en tienda",
                isSelected = selectedOption == DeliveryOption.EnTienda,
                onClick = { viewModel.onOptionSelected(DeliveryOption.EnTienda) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DeliveryOptionCard(
                icon = Icons.Default.Store,
                title = "Retiro en tienda",
                isSelected = selectedOption == DeliveryOption.Retiro,
                onClick = { viewModel.onOptionSelected(DeliveryOption.Retiro) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onContinue,
                enabled = selectedOption != null && (selectedOption != DeliveryOption.Moto || address.isNotBlank()),
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
fun DeliveryOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    isSelected: Boolean,
    content: @Composable (() -> Unit)? = null,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(description)
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}
