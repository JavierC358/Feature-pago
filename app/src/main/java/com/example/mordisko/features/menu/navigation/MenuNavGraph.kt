package com.example.mordisko.features.menu.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mordisko.features.cart.presentation.CartViewModel
import com.example.mordisko.features.menu.domain.model.getPizzaItemsForCategory
import com.example.mordisko.features.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.menu.presentation.screens.PizzaDetailScreen
import com.example.mordisko.features.menu.presentation.viewmodel.MenuViewModel

fun NavGraphBuilder.menuNavGraph(
    navController: NavHostController,
    cartViewModel: CartViewModel
) {
    composable("menu/{category}") { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: ""
        val pizzas = getPizzaItemsForCategory(category)
        val viewModel: MenuViewModel = hiltViewModel()

        MenuPizzasScreen(
            navController = navController,
            category = category,
            pizzas = pizzas,
            cartViewModel = cartViewModel
        )
    }

    composable("pizza_detail") {
        val viewModel: MenuViewModel = hiltViewModel()
        val selectedPizza = viewModel.selectedPizza.collectAsState().value

        selectedPizza?.let { pizza ->
            PizzaDetailScreen(
                pizza = pizza,
                onBack = {
                    viewModel.clearSelectedPizza()
                    navController.popBackStack()
                },
                navController = navController,
                cartViewModel = cartViewModel // ✅ ViewModel compartido
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("❌ No hay pizza seleccionada", color = Color.Red)
            }
        }
    }
}