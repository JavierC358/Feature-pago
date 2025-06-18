package com.example.mordisko.features.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.mordisko.features.cart.presentation.CartViewModel
import com.example.mordisko.features.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.dashboard.screen.SupportScreen
import com.example.mordisko.features.home.HomeScreen

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
        BottomNavItem("support", "Soporte", Icons.Default.Phone)
    )
    val localNavController = rememberNavController()
    var selectedItem by remember { mutableStateOf("home") }

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
                    onLogout = {
                        onLogout()
                    }
                )
            }

            composable("stats") {
                OrdersStatsScreen()
            }

            composable("support") {
                SupportScreen()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)