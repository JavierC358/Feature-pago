package com.example.mordisko.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.features.admin.screens.AdminVerificacionesScreen
import com.example.mordisko.features.user.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.features.user.authentication.presentation.login.ForgotPasswordScreen
import com.example.mordisko.features.user.authentication.presentation.login.LoginScreen
import com.example.mordisko.features.user.authentication.presentation.login.RegisterScreen
import com.example.mordisko.features.user.authentication.presentation.splash.SplashScreen
import com.example.mordisko.features.user.cart.presentation.CartScreen
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.cart.presentation.DeliveryScreen
import com.example.mordisko.features.user.cart.presentation.OrderStatusScreen
import com.example.mordisko.features.user.cart.presentation.OrderSummaryScreen
import com.example.mordisko.features.user.cart.presentation.PaymentMethodScreen
import com.example.mordisko.features.user.cart.presentation.VerificarPagoScreen
import com.example.mordisko.features.user.cart.presentation.maps.MapScreen
import com.example.mordisko.features.user.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.user.dashboard.screen.SupportScreen
import com.example.mordisko.features.user.home.HomeScreen
import com.example.mordisko.features.user.menu.domain.model.getPizzaItemsForCategory
import com.example.mordisko.features.user.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.user.menu.presentation.viewmodel.MenuViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    googleLauncher: ActivityResultLauncher<Intent>,
    googleAuthViewModel: GoogleAuthViewModel,
    cartViewModel: CartViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomBar = currentRoute.startsWith(Routes.Home) ||
            currentRoute.startsWith("${Routes.Menu}/") ||
            currentRoute == Routes.Cart ||
            currentRoute == Routes.Delivery

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Splash) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Login) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Routes.ForgotPassword)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register)
                    },
                    googleLauncher = googleLauncher,
                    googleAuthViewModel = googleAuthViewModel
                )
            }

            composable(Routes.Register) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Register) { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Register) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ForgotPassword) {
                ForgotPasswordScreen(
                    onBackToLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.ForgotPassword) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Home) {
                HomeScreen(
                    onCategorySelected = { category ->
                        navController.navigate("${Routes.Menu}/$category")
                    },
                    onLogout = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }
                )
            }

            composable("${Routes.Menu}/{category}") { backStackEntry ->
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

            composable(Routes.Cart) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    navController = navController,
                    onContinue = {
                        navController.navigate(Routes.Delivery)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.Delivery) {
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
                        navController.navigate(Routes.OrderSummary)
                    }
                )
            }

            composable(Routes.Map) {
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

            composable(Routes.OrderSummary) {
                OrderSummaryScreen(
                    cartViewModel = cartViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("${Routes.OrderStatus}/{orderNumber}/{montoTotal}") { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: "Desconocido"
                val montoTotal = backStackEntry.arguments?.getString("montoTotal")?.toDoubleOrNull() ?: 0.0

                OrderStatusScreen(
                    orderNumber = orderNumber,
                    montoTotal = montoTotal,
                    onComprobarPago = {
                        navController.navigate(Routes.verificarPagoRoute(orderNumber))
                    },
                    onCancelar = {
                        navController.navigate(Routes.Home) {
                            popUpTo("${Routes.OrderStatus}/{orderNumber}/{montoTotal}") { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.VerificarPagoWithArg) { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""

                VerificarPagoScreen(
                    orderNumber = orderNumber,
                    onCerrar = {
                        navController.popBackStack()
                    }
                )
            }

            composable("stats") {
                OrdersStatsScreen()
            }

            composable("support") {
                SupportScreen()
            }

            composable(Routes.AdminVerificaciones) {
                AdminVerificacionesScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val bottomNavItems = listOf(
        BottomNavItem(Routes.Home, "Inicio", Icons.Default.Home),
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
