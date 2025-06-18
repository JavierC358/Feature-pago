package com.example.mordisko.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
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
import com.example.mordisko.features.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.features.authentication.presentation.login.*
import com.example.mordisko.features.authentication.presentation.splash.SplashScreen
import com.example.mordisko.features.cart.presentation.*
import com.example.mordisko.features.cart.presentation.maps.MapScreen
import com.example.mordisko.features.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.dashboard.screen.SupportScreen
import com.example.mordisko.features.home.HomeScreen
import com.example.mordisko.features.menu.domain.model.getPizzaItemsForCategory
import com.example.mordisko.features.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.menu.presentation.viewmodel.MenuViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    googleLauncher: ActivityResultLauncher<Intent>,
    googleAuthViewModel: GoogleAuthViewModel,
    cartViewModel: CartViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomBar = currentRoute.startsWith("home") ||
            currentRoute.startsWith("menu/") ||
            currentRoute == "cart" ||
            currentRoute == "delivery"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate("forgot_password")
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    googleLauncher = googleLauncher,
                    googleAuthViewModel = googleAuthViewModel
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onBackToLogin = {
                        navController.navigate("login") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    onCategorySelected = { category ->
                        navController.navigate("menu/$category")
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

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

            composable("cart") {
                CartScreen(
                    cartViewModel = cartViewModel,
                    navController = navController,
                    onContinue = {
                        navController.navigate("delivery")
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("delivery") {
                DeliveryScreen(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = {
                        navController.navigate("payment_method")
                    }
                )
            }

            composable("payment_method") {
                PaymentMethodScreen(
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = {
                        navController.navigate("order_summary")
                    }
                )
            }

            composable("map") {
                MapScreen(
                    cartViewModel = cartViewModel,
                    onConfirm = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("order_summary") {
                OrderSummaryScreen(
                    cartViewModel = cartViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("order_status/{orderNumber}") { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: "Desconocido"
                OrderStatusScreen(
                    orderNumber = orderNumber,
                    onBackToHome = {
                        navController.navigate("home") {
                            popUpTo("order_status/{orderNumber}") { inclusive = true }
                        }
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

@Composable
fun BottomBar(navController: NavHostController) {
    val bottomNavItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Default.Home),
        BottomNavItem("stats", "Pedidos", Icons.Default.BarChart),
        BottomNavItem("support", "Soporte", Icons.Default.Phone)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)