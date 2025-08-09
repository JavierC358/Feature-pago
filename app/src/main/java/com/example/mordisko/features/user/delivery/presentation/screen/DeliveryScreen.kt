package com.example.mordisko.features.user.delivery.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryOption
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val reference by cartViewModel.addressReference.collectAsState()
    val deliveryCost by cartViewModel.deliveryCost.collectAsState()
    val selectedLat by cartViewModel.selectedLat.collectAsState()
    val selectedLng by cartViewModel.selectedLng.collectAsState()

    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    // ✅ Cargar settings de Firestore una sola vez
    LaunchedEffect(Unit) {
        viewModel.loadDeliverySettings()
    }

    // ✅ Calcular costo cuando el cliente seleccione dirección y opción Moto
    LaunchedEffect(selectedLat, selectedLng, selectedOption) {
        if (selectedOption == DeliveryOption.Moto && selectedLat != null && selectedLng != null) {
            val costo = viewModel.calcularCostoDelivery(selectedLat!!, selectedLng!!)
            cartViewModel.setDeliveryCost(costo)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        // 🚲 Opción Moto
        DeliveryOptionCard(
            icon = Icons.Default.DeliveryDining,
            title = "Moto",
            description = when {
                selectedOption != DeliveryOption.Moto -> null
                selectedLat == null || selectedLng == null -> "Seleccione dirección para calcular costo"
                else -> "Costo del servicio: $${"%.2f".format(deliveryCost)}"
            },
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
                            OutlinedTextField(
                                value = reference,
                                onValueChange = {
                                    if (it.length <= 160) {
                                        cartViewModel.onAddressReferenceChanged(it)
                                    }
                                },
                                label = { Text("Referencia adicional (Obligatorio).", color = Color.White) },
                                supportingText = { Text("${reference.length}/160", color = Color.White) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFFE05B13), // ✅ Color personalizado para borde activo
                                    unfocusedBorderColor = Color(0xFFE05B13), // ✅ Color personalizado para borde inactivo
                                    cursorColor = Color.White,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    focusedSupportingTextColor = Color.White,
                                    unfocusedSupportingTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate("map") },
                            colors = ButtonDefaults.buttonColors(containerColor = orange),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Seleccionar dirección en el mapa", color = Color.White)
                        }
                    }
                }
            },
            onClick = {
                viewModel.onOptionSelected(DeliveryOption.Moto)
                cartViewModel.setDeliveryOption(DeliveryOption.Moto) },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🍽️ Opción Consumo en tienda
        DeliveryOptionCard(
            icon = Icons.Default.LocalDining,
            title = "Consumo en tienda",
            isSelected = selectedOption == DeliveryOption.EnTienda,
            onClick = {
                viewModel.onOptionSelected(DeliveryOption.EnTienda)
                cartViewModel.setDeliveryOption(DeliveryOption.EnTienda)
                cartViewModel.setDeliveryCost(0.0) // ✅ limpia costo
            },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🏪 Opción Retiro en tienda
        DeliveryOptionCard(
            icon = Icons.Default.Store,
            title = "Retiro en tienda",
            isSelected = selectedOption == DeliveryOption.Retiro,
            onClick = {
                viewModel.onOptionSelected(DeliveryOption.Retiro)
                cartViewModel.setDeliveryOption(DeliveryOption.Retiro)
                cartViewModel.setDeliveryCost(0.0) // ✅ limpia costo
            },
            highlightColor = lightOrange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Botón Continuar con la lógica corregida
        Button(
            onClick = onContinue,
            enabled = when (selectedOption) {
                DeliveryOption.Moto -> address.isNotBlank() && reference.isNotBlank() && deliveryCost > 0
                DeliveryOption.EnTienda, DeliveryOption.Retiro -> true
                else -> false
            },
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
    highlightColor: Color = Color(0xFFFFA726)
) {
    val backgroundColor = if (isSelected) highlightColor else Color.White
    val textColor = if (isSelected) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFFE05B13))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
            }
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = textColor)
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}