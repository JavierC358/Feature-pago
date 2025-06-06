package com.example.mordisko.features.menu.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mordisko.features.menu.domain.model.PizzaItem
import com.example.mordisko.features.menu.presentation.viewmodel.MenuViewModel

@Composable
fun MenuPizzasScreen(
    navController: NavController,
    category: String,
    pizzas: List<PizzaItem>
) {
    val viewModel: MenuViewModel = hiltViewModel()
    val selectedPizza by viewModel.selectedPizza.collectAsState()

    // 👇 Si hay pizza seleccionada, mostramos su pantalla de detalle
    if (selectedPizza != null) {
        PizzaDetailScreen(
            pizza = selectedPizza!!,
            onBack = {
                viewModel.clearSelectedPizza()
            }
        )
        return // 👉 Importante: evitar que se siga renderizando el grid
    }

    // 👇 Esta es la pantalla habitual con el grid
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔙 Botón VOLVER
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

        // 🏷️ Título de la categoría
        Text(
            text = "Categoría: $category",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 🍕 Grid de pizzas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(pizzas) { pizza ->
                PizzaCard(
                    pizza = pizza,
                    onClick = {
                        viewModel.selectPizza(pizza)
                        // ❌ Ya no navegamos con navController
                        // navController.navigate("pizza_detail")
                        // porque el detalle se muestra aquí mismo
                    }
                )
            }
        }
    }
}