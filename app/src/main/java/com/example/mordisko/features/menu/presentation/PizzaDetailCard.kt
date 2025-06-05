package com.example.mordisko.features.menu.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mordisko.features.menu.domain.model.PizzaItem

@Composable
fun PizzaDetailCard(
    pizza: PizzaItem,
    onDismiss: () -> Unit,
    onBack: () -> Unit
) {
    var selectedSize by remember { mutableStateOf("Med") }
    val sizes = listOf("EG", "Gde", "Med", "Peq")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                    Text(
                        text = "VOLVER",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable { onBack() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = pizza.imageRes),
                    contentDescription = pizza.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = pizza.name, style = MaterialTheme.typography.titleLarge)
                Text(text = pizza.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Tamaño:", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sizes.forEach { size ->
                        Text(
                            text = size,
                            modifier = Modifier
                                .padding(8.dp)
                                .selectable(
                                    selected = (size == selectedSize),
                                    onClick = { selectedSize = size }
                                )
                                .background(
                                    if (size == selectedSize) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Agregar al carrito",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(36.dp)
                )
            }
        }
    )
}