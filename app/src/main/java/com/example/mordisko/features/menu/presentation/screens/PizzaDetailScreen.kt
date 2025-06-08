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
    val textColor = Color(0xFFE05B13)
    val bolivaresPrice = pizza.priceUsd * 36.5 // Ejemplo de tasa de conversión

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
                modifier = Modifier.align(Alignment.Start)
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = textColor)
                }
                Text(
                    text = "Volver",
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(start = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // 🖼️ Imagen
            Image(
                painter = painterResource(id = pizza.imageRes),
                contentDescription = pizza.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // 💵 Precio en dólares
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${pizza.priceUsd}",
                color = textColor,
                style = MaterialTheme.typography.titleLarge
            )

            // 🪙 Precio en bolívares
            Text(
                text = "Bs ${"%,.2f".format(bolivaresPrice)}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pizza.name,
                style = MaterialTheme.typography.titleLarge,
                color = textColor
            )
            Text(
                text = pizza.description,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showSizes) {
                Text("Tamaño:", style = MaterialTheme.typography.titleMedium, color = textColor)
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
                                    if (size == selectedSize) textColor.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 🛒 Controles de cantidad con carrito
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
                    Icon(Icons.Default.Remove, contentDescription = "Restar", tint = textColor)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = {
                    // Acción al agregar al carrito
                }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        tint = textColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar", tint = textColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cantidad: $quantity",
                style = MaterialTheme.typography.titleLarge,
                color = textColor
            )
        }
    }
}