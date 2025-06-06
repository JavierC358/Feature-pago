package com.example.mordisko.features.menu.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mordisko.features.menu.domain.model.PizzaItem
import com.example.mordisko.features.menu.domain.model.PizzaItemCategory
import android.util.Log

@Composable
fun PizzaDetailScreen(
    pizza: PizzaItem,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        Log.d("PizzaDetailScreen", "Mostrando detalles de: ${pizza.name}")
    }

    var selectedSize by remember { mutableStateOf("Med") }
    var quantity by remember { mutableStateOf(1) }
    val sizes = listOf("EG", "Gde", "Med", "Peq")

    val categoriesWithSizes = listOf(
        PizzaItemCategory.PIZZAS,
        PizzaItemCategory.DEDOS_DE_QUESO,
        PizzaItemCategory.EXTRAS
    )

    val showSizes = pizza.category in categoriesWithSizes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // 🔙 Botón VOLVER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.Start)
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Volver",
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(start = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🖼️ Imagen
            Image(
                painter = painterResource(id = pizza.imageRes),
                contentDescription = pizza.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = pizza.name, style = MaterialTheme.typography.titleLarge)
            Text(text = pizza.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            if (showSizes) {
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
            }

            // 🛒 Cantidad con carrito en el centro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (quantity > 1) quantity--
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = {
                    // Acción al agregar al carrito
                }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Cantidad: $quantity", style = MaterialTheme.typography.titleLarge)
        }
    }
}