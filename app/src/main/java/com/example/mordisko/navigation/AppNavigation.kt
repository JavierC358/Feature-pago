package com.example.mordisko.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.core.navigation.Routes.ACTUALIZAR_IMAGENES_SCREEN
import com.example.mordisko.core.navigation.Routes.EDIT_PRICES_SCREEN
import com.example.mordisko.core.navigation.Routes.GESTIONAR_PRODUCTOS_SCREEN
import com.example.mordisko.core.navigation.Routes.REPORTES_FECHA_SCREEN
import com.example.mordisko.core.navigation.Routes.VERIFICAR_ORDENES_SCREEN
import com.example.mordisko.core.navigation.Routes.crearProductoRoute
import com.example.mordisko.features.admin.categories.presentation.AdminCategoriesScreen
import com.example.mordisko.features.admin.presentation.viewmodel.PaymentVerificationViewModel
import com.example.mordisko.features.admin.presentation.screens.ActualizarImagenesScreen
import com.example.mordisko.features.admin.presentation.screens.ActualizarTasaScreen
import com.example.mordisko.features.admin.presentation.screens.AdminDashboardScreen
import com.example.mordisko.features.admin.presentation.screens.AdminResumenPedidosScreen
import com.example.mordisko.features.admin.presentation.screens.AdminVerificacionesScreen
import com.example.mordisko.features.admin.presentation.screens.CrearProductoScreen
import com.example.mordisko.features.admin.presentation.screens.EditPricesScreen
import com.example.mordisko.features.admin.presentation.screens.EditarDescripcionScreen
import com.example.mordisko.features.admin.presentation.screens.EditarPagoMovilScreen
import com.example.mordisko.features.admin.presentation.screens.GestionarProductosScreen
import com.example.mordisko.features.admin.presentation.screens.OrderDetailScreen
import com.example.mordisko.features.admin.presentation.screens.ResumenDeOrdenesScreen
import com.example.mordisko.features.admin.presentation.viewmodel.OrderDetailViewModel
import com.example.mordisko.features.help.faqs.presentation.PreguntasFrecuentesScreen
import com.example.mordisko.features.user.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.features.user.authentication.presentation.login.ElegirRolScreen
import com.example.mordisko.features.user.authentication.presentation.login.ForgotPasswordScreen
import com.example.mordisko.features.user.authentication.presentation.login.HorarioScreen
import com.example.mordisko.features.user.authentication.presentation.login.LoginScreen
import com.example.mordisko.features.user.authentication.presentation.login.PoliticaPrivacidadScreen
import com.example.mordisko.features.user.authentication.presentation.login.RegisterScreen
import com.example.mordisko.features.user.authentication.presentation.login.TerminosCondicionesScreen
import com.example.mordisko.features.user.authentication.presentation.splash.SplashScreen
import com.example.mordisko.features.user.cart.presentation.CartScreen
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.cart.presentation.OrderStatusScreen
import com.example.mordisko.features.user.cart.presentation.OrderSummaryScreen
import com.example.mordisko.features.user.cart.presentation.PagoEnVerificacionScreen
import com.example.mordisko.features.user.cart.presentation.PaymentMethodScreen
import com.example.mordisko.features.user.cart.presentation.PedidoEnProcesoScreen
import com.example.mordisko.features.user.cart.presentation.PedidoVerificadoScreen
import com.example.mordisko.features.user.cart.presentation.VerificarPagoScreen
import com.example.mordisko.features.user.cart.presentation.maps.MapScreen
import com.example.mordisko.features.user.dashboard.screen.OrdersStatsScreen
import com.example.mordisko.features.user.delivery.presentation.screen.DeliveryScreen
import com.example.mordisko.features.user.history.presentation.screen.HistoryScreen
import com.example.mordisko.features.user.history.presentation.viewmodel.OrderHistoryViewModel
import com.example.mordisko.features.user.home.HomeScreen
import com.example.mordisko.features.user.menu.presentation.screens.MenuPizzasScreen
import com.example.mordisko.features.user.profile.presentation.EditarPerfilScreen
import com.example.mordisko.features.user.profile.presentation.EditarPerfilViewModel
import com.example.mordisko.features.user.support.SupportScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
                    onNavigateToHorario = {
                        navController.navigate(Routes.Horario) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    },
                    onNavigateToAdminPanel = {
                        navController.navigate("admin_dashboard") {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Login) {
                LoginScreen(
                    navController = navController, // ✅ agrega esto
                    onLoginSuccess = {
                        navController.navigate(Routes.Horario) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    },
                    onNavigateToAdminPanel = {
                        navController.navigate("elegir_rol") { // ✅ cambia a elegir_rol si lo deseas
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
                        navController.navigate(Routes.Horario) {
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

            composable(Routes.Horario) {
                HorarioScreen(
                    onContinuar = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Horario) { inclusive = true }
                        }
                    },
                    onTerminos = { navController.navigate(Routes.TerminosCondiciones) },
                    onPoliticas = { navController.navigate(Routes.PoliticaPrivacidad) },
                    onAyuda = { navController.navigate(Routes.Faqs) },
                    onEditarPerfil = { navController.navigate(Routes.EditarPerfil) },

                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Routes.Login) {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("elegir_rol") {
                ElegirRolScreen(
                    onClienteSelected = {
                        navController.navigate(Routes.Home) {
                            popUpTo("elegir_rol") { inclusive = true }
                        }
                    },
                    onAdminSelected = {
                        navController.navigate(Routes.AdminDashboard.route) {
                            popUpTo("elegir_rol") { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.TerminosCondiciones) {
                TerminosCondicionesScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.PoliticaPrivacidad) {
                PoliticaPrivacidadScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.Ayuda) {
                PreguntasFrecuentesScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.EditarPerfil) {
                val viewModel: EditarPerfilViewModel = hiltViewModel() // 👈 Aquí sí es válido
                val context = LocalContext.current

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let { selectedUri ->
                        viewModel.onImageSelected(selectedUri)
                    }
                }

                EditarPerfilScreen(
                    onBack = { navController.popBackStack() },
                    onGuardarExitoso = { navController.popBackStack() },
                    pickImage = {
                        launcher.launch("image/*")
                    }
                )
            }

            composable(Routes.Home) {
                HomeScreen(
                    onCategorySelected = { category ->
                        navController.navigate("${Routes.Menu}/$category")
                    },
                    onBackToHorario = {
                        navController.navigate(Routes.Horario) {
                            popUpTo(Routes.Home) { inclusive = false }
                        }
                    }
                )
            }

            composable("${Routes.Menu}/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""

                MenuPizzasScreen(
                    navController = navController,
                    category = category,
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
                    },
                    viewModel = cartViewModel
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

            composable(
                route = Routes.PEDIDO_EN_PROCESO + "/{orderNumber}"
            ) { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""
                PedidoEnProcesoScreen(
                    orderNumber = orderNumber,
                    onBackToHome = {
                        navController.navigate(Routes.Home) { // 👈 corregido: Home con H mayúscula
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }
                )
            }

            composable("${Routes.OrderStatus}/{orderNumber}/{montoTotal}") { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: "Desconocido"
                val montoTotal = backStackEntry.arguments
                    ?.getString("montoTotal")
                    ?.replace(',', '.')   // ← admite "123,45" y "123.45"
                    ?.toDoubleOrNull()
                    ?: 0.0

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
                    onCerrar = { navController.popBackStack() }, // ← sigue cerrando normal
                    onVerificacionEnviada = { ord ->
                        navController.navigate("payment_pending/$ord") {
                            // Opcional: limpia el form del back stack si quieres
                            // popUpTo(Routes.VerificarPagoWithArg) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable("stats") {
                OrdersStatsScreen()
            }

            composable(Routes.Support) {
                SupportScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(VERIFICAR_ORDENES_SCREEN) {
                AdminVerificacionesScreen(navController = navController)
            }

            composable(Routes.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigateToVerificaciones = {
                        navController.navigate(VERIFICAR_ORDENES_SCREEN)
                    },
                    onNavigateToResumen = {
                        navController.navigate(REPORTES_FECHA_SCREEN)
                    },
                    onNavigateToEditPrices = {
                        navController.navigate(EDIT_PRICES_SCREEN)
                    },
                    onNavigateToActualizarImagenes = {
                        navController.navigate(ACTUALIZAR_IMAGENES_SCREEN)
                    },
                    onNavigateToActualizarTasa = {
                        navController.navigate("actualizar_tasa")
                    },
                    onNavigateToEditarDescripcion = {
                        navController.navigate("editar_descripcion")
                    },
                    onNavigateToEditarPagoMovil = {
                        navController.navigate("editar_pago_movil")
                    },
                    onNavigateToGestionarProductos = {
                        navController.navigate(GESTIONAR_PRODUCTOS_SCREEN)
                    },
                    onNavigateToCrearProducto = {
                        navController.navigate("crear_producto")
                    },
                    // 🔹 Nuevo callback para la tarjeta de categorías
                    onNavigateToGestionarCategorias = {
                        navController.navigate(Routes.ROUTE_ADMIN_CATEGORIES) // aquí defines la ruta que abrirá la pantalla de categorías
                    },
                    onLogout = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.AdminDashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(EDIT_PRICES_SCREEN) {
                EditPricesScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            composable("pedido_verificado") {
                PedidoVerificadoScreen(
                    cartViewModel = cartViewModel, // ✅ inyectado correctamente
                    navController = navController
                )
            }

            composable("payment_pending/{orderNumber}") { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""
                PagoEnVerificacionScreen(
                    orderNumber = orderNumber,
                    navController = navController
                )
            }

            composable(
                route = Routes.OrderDetail,
                arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: return@composable

                val viewModel: OrderDetailViewModel = hiltViewModel()
                val orderState by viewModel.order.collectAsState()

                OrderDetailScreen(
                    navController = navController,
                    orderNumber = orderNumber
                )
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

            composable(REPORTES_FECHA_SCREEN) {
                AdminResumenPedidosScreen(
                    onConsultarClick = { desde, hasta ->
                        navController.navigate("resumen_screen/${desde.time}/${hasta.time}")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("menu_screen/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                MenuPizzasScreen(
                    navController = navController,
                    category = category,
                    cartViewModel = cartViewModel
                )
            }

            composable(ACTUALIZAR_IMAGENES_SCREEN) {
                ActualizarImagenesScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("actualizar_tasa") {
                ActualizarTasaScreen(navController)
            }

            composable("editar_descripcion") {
                EditarDescripcionScreen(navController)
            }

            composable(Routes.EditarPagoMovil) {
                EditarPagoMovilScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(GESTIONAR_PRODUCTOS_SCREEN) {
                GestionarProductosScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(crearProductoRoute) {
                CrearProductoScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.ROUTE_ADMIN_CATEGORIES) {
                val context = LocalContext.current
                var pendingCategoryId by rememberSaveable { mutableStateOf<String?>(null) }

                // Picker
                val pickImage = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    val catId = pendingCategoryId
                    pendingCategoryId = null
                    if (uri == null || catId == null) return@rememberLauncherForActivityResult

                    // 1) Subir a Storage
                    val storageRef = FirebaseStorage.getInstance()
                        .reference.child("category_images/$catId.jpg")

                    storageRef.putFile(uri)
                        .addOnSuccessListener {
                            // 2) Obtener URL de descarga
                            storageRef.downloadUrl
                                .addOnSuccessListener { downloadUri ->
                                    val url = downloadUri.toString()

                                    // 3) Actualizar imageUrl en Firestore
                                    FirebaseFirestore.getInstance()
                                        .collection("categories")
                                        .document(catId)
                                        .update("imageUrl", url)
                                        .addOnSuccessListener {
                                            // Opcional: feedback visual
                                            // Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            // Toast.makeText(context, "Error guardando URL: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    // Toast.makeText(context, "Error obteniendo URL: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            // Toast.makeText(context, "Error subiendo imagen: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }

                AdminCategoriesScreen(
                    onBack = { navController.popBackStack() },
                    onPickImageForCategory = { categoryId ->
                        pendingCategoryId = categoryId
                        pickImage.launch("image/*")
                    }
                )
            }
            composable(Routes.History) {
                val historyViewModel: OrderHistoryViewModel = hiltViewModel()

                HistoryScreen(
                    viewModel = historyViewModel,
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onRepetirPedido = {
                        navController.navigate(Routes.Cart)
                    }
                )
            }

            composable(Routes.Faqs) {
                PreguntasFrecuentesScreen(
                    onBack = { navController.popBackStack() } // ← volver
                )
            }

        }

        }
    }

@Composable
fun BottomBar(navController: NavHostController) {
    val bottomNavItems = listOf(
        BottomNavItem(Routes.Home, "Inicio", Icons.Default.Home),
        BottomNavItem(Routes.History, "Pedidos", Icons.Default.BarChart),
        BottomNavItem("support", "Soporte", Icons.Default.Phone),
        BottomNavItem(Routes.Cart, "Carrito", Icons.Default.ShoppingCart)
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
