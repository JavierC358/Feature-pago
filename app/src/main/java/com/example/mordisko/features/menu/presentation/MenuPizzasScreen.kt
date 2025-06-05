package com.example.mordisko.features.menu.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mordisko.features.menu.domain.model.PizzaItem

@Composable
fun MenuPizzasScreen(
    navController: NavController,
    category: String,
    pizzas: List<PizzaItem>,
    onPizzaClick: (PizzaItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ Sección VOLVER + Flecha
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { navController.popBackStack() }
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "VOLVER",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // ✅ Título de la categoría
        Text(
            text = "Categoría: $category",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ✅ Tarjetas de pizzas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(pizzas) { pizza ->
                PizzaCard(pizza = pizza, onClick = { onPizzaClick(pizza) })
            }
        }
    }
}