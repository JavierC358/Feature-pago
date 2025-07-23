package com.example.mordisko.features.user.menu.presentation.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.presentation.viewmodel.MenuViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun MenuPizzasScreen(
    navController: NavController,
    category: String,
    cartViewModel: CartViewModel
) {
    val viewModel: MenuViewModel = hiltViewModel()
    val selectedPizza by viewModel.selectedPizza.collectAsState()
    val products by viewModel.products.collectAsState()

    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(orange, lightOrange)
    )

    if (selectedPizza != null) {
        PizzaDetailScreen(
            pizza = selectedPizza!!,
            onBack = {
                viewModel.clearSelectedPizza()
            },
            navController = navController,
            cartViewModel = cartViewModel
        )
        return
    }

    // Convertimos la categoría String a enum para comparar con la lista de productos
    val categoryEnum = try {
        PizzaItemCategory.valueOf(category.uppercase())
    } catch (e: Exception) {
        null
    }

    if (categoryEnum == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Categoría inválida",
                color = Color.Red,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    val filteredProducts = products.filter { it.category == categoryEnum }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(10.dp)
    ) {
        // 🔙 Botón Volver
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { navController.popBackStack() }
                .padding(bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Volver",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        // 🧀 Título con el nombre de la categoría
        Text(
            text = category.replace("_", " ").capitalize(),
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredProducts) { pizza ->
                PizzaCard(
                    pizza = pizza,
                    onClick = {
                        viewModel.selectPizza(pizza)
                    },
                    nameColor = orange,
                    paddingHorizontal = 2.dp
                )
            }
        }
    }
}