package com.example.mordisko.navigation

import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.features.admin.screens.AdminDashboardScreen
import com.example.mordisko.features.admin.screens.AdminResumenPedidosScreen
import com.example.mordisko.features.admin.screens.AdminVerificacionesScreen
import com.example.mordisko.features.admin.screens.OrderDetailScreen
import com.example.mordisko.features.admin.viewmodel.OrderDetailViewModel
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
import com.example.mordisko.features.user.cart.presentation.PedidoVerificadoScreen
import com.example.mordisko.features.user.cart.presentation.VerificarPagoScreen
import com.example.mordisko.features.user.cart.presentation.maps.MapScreen
import com.example.mordisko.features.user.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.user.dashboard.screen.SupportScreen
import com.example.mordisko.features.user.home.HomeScreen
import com.example.mordisko.features.user.menu.domain.model.getPizzaItemsForCategory
import com.example.mordisko.features.user.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.user.menu.presentation.viewmodel.MenuViewModel
import com.example.mordisko.features.admin.screens.OrderDetailScreen
import com.example.mordisko.features.admin.screens.ResumenDeOrdenesScreen
import java.util.Date

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
                    },
                    onNavigateToAdminPanel = {
                        navController.navigate("admin_dashboard") { // ✅ nueva ruta
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
                    onNavigateToAdminPanel = {
                        navController.navigate(Routes.AdminVerificaciones) {
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
                            popUpTo(Routes.OrderStatus) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    navController = navController
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
                AdminVerificacionesScreen(navController = navController)
            }

            composable("admin_dashboard") {
                AdminDashboardScreen(
                    onNavigateToVerificaciones = {
                        navController.navigate(Routes.AdminVerificaciones)
                    },
                    onNavigateToResumen = {
                        navController.navigate("resumen_admin_screen")
                    },
                    onLogout = {
                        navController.navigate(Routes.Login) {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }
                )
            }

            composable("pedido_verificado") {
                PedidoVerificadoScreen(
                    cartViewModel = cartViewModel, // ✅ inyectado correctamente
                    onFinalizar = {
                        navController.navigate(Routes.Home) {
                            popUpTo("pedido_verificado") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.OrderDetail,
                arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: return@composable

                val viewModel: OrderDetailViewModel = hiltViewModel()
                val orderState by viewModel.order.collectAsState()

                LaunchedEffect(orderNumber) {
                    Log.d("Pantalla", "OrderNumber recibido: $orderNumber")
                    viewModel.loadOrder(orderNumber)
                }

                orderState?.let { order ->
                    OrderDetailScreen(
                        navController = navController,
                        orderNumber = orderNumber
                    )
                }
            }

            composable("resumen_screen/{desde}/{hasta}") { backStackEntry ->
                val desdeMillis = backStackEntry.arguments?.getString("desde")?.toLongOrNull()
                val hastaMillis = backStackEntry.arguments?.getString("hasta")?.toLongOrNull()

                if (desdeMillis != null && hastaMillis != null) {
                    ResumenDeOrdenesScreen(
                        fechaDesde = Date(desdeMillis),
                        fechaHasta = Date(hastaMillis),
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable("resumen_admin_screen") {
                AdminResumenPedidosScreen(
                    onConsultarClick = { desde, hasta ->
                        // Navega a la pantalla de resultados pasando las fechas
                        navController.navigate("resumen_screen/${desde.time}/${hasta.time}")
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
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
