package com.example.mordisko.features.user.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mordisko.features.user.cart.presentation.CartScreen
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.user.support.SupportScreen
import com.example.mordisko.features.user.home.HomeScreen

@Composable
fun DashboardScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    onLogout: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Default.Home),
        BottomNavItem("stats", "Pedidos", Icons.Default.BarChart),
        BottomNavItem("support", "Soporte", Icons.Default.Phone),
        BottomNavItem("cart", "Carrito", Icons.Default.ShoppingCart)
    )
    val localNavController = rememberNavController()
    var selectedItem by remember { mutableStateOf("home") }

    val currentBackStackEntry by localNavController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        val currentRoute = currentBackStackEntry?.destination?.route
        if (currentRoute in bottomNavItems.map { it.route }) {
            selectedItem = currentRoute ?: "home"
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item.route,
                        onClick = {
                            selectedItem = item.route
                            val currentDestination =
                                localNavController.currentBackStackEntry?.destination?.route

                            if (item.route == "home") {
                                localNavController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            } else if (currentDestination != item.route) {
                                localNavController.navigate(item.route) {
                                    popUpTo(localNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = localNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onCategorySelected = { category ->
                        onCategorySelected(category)
                    },
                )
            }

            composable("stats") {
                OrdersStatsScreen()
            }

            composable("support") {
                SupportScreen(
                    onBack = { localNavController.popBackStack() }
                )
            }

            composable("cart") {
                CartScreen(
                    cartViewModel = cartViewModel,
                    navController = navController,
                    onContinue = { navController.navigate("delivery") },
                    onBack = { localNavController.popBackStack() },
                    viewModel = cartViewModel // ✅ sí, lo estás usando dos veces pero necesario para compatibilidad
                )
            }
        }
    }
}


data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)