package com.example.mordisko.features.menu.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mordisko.features.menu.domain.mock.pizzasClasicas
import com.example.mordisko.features.menu.domain.model.PizzaItem
import com.example.mordisko.features.menu.presentation.MenuPizzasScreen
import com.example.mordisko.features.menu.presentation.PizzaDetailCard

fun NavGraphBuilder.menuNavGraph(navController: NavHostController) {
    composable(
        route = "menu/{category}",
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: "Sin categoría"
        val pizzas = when (category) {
            "Pizzas" -> pizzasClasicas
            else -> emptyList()
        }

        var selectedPizza by remember { mutableStateOf<PizzaItem?>(null) }

        MenuPizzasScreen(
            navController = navController,
            category = category,
            pizzas = pizzas,
            onPizzaClick = { selectedPizza = it }
        )

        selectedPizza?.let {
            PizzaDetailCard(
                pizza = it,
                onDismiss = { selectedPizza = null },
                onBack = { selectedPizza = null }
            )
        }
    }
}