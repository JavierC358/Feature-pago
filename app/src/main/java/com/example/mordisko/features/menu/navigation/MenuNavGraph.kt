package com.example.mordisko.features.menu.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel // 👈 Import necesario
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mordisko.features.menu.domain.model.getPizzaItemsForCategory
import com.example.mordisko.features.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.menu.presentation.screens.PizzaDetailScreen
import com.example.mordisko.features.menu.presentation.viewmodel.MenuViewModel

fun NavGraphBuilder.menuNavGraph(navController: NavHostController) {

    // 🧩 Listado de pizzas por categoría
    composable("menu/{category}") { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: ""
        val pizzas = getPizzaItemsForCategory(category)

        val viewModel: MenuViewModel = viewModel() // 👈 ViewModel compartido a nivel Activity

        MenuPizzasScreen(
            navController = navController,
            category = category,
            pizzas = pizzas
        )
    }

    // 🍕 Detalle de la pizza seleccionada
    composable("pizza_detail") {
        val viewModel: MenuViewModel = viewModel() // 👈 Mismo ViewModel compartido
        val selectedPizza by viewModel.selectedPizza.collectAsState()

        selectedPizza?.let { pizza ->
            PizzaDetailScreen(
                pizza = pizza,
                onBack = {
                    viewModel.clearSelectedPizza()
                    navController.popBackStack()
                }
            )
        } ?: run {
            // 🔴 Mensaje de depuración si no hay pizza seleccionada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "❌ No hay pizza seleccionada", color = Color.Red)
            }
        }
    }
}