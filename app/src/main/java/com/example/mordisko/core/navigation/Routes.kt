package com.example.mordisko.core.navigation

object Routes {
    // Pantallas generales
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"
    const val Home = "home"
    const val Menu = "menu"
    const val Cart = "cart"
    const val Delivery = "delivery"
    const val Map = "map"
    const val OrderSummary = "order_summary"
    const val OrderStatus = "order_status"

    // Verificación de pago (cliente)
    const val VerificarPago = "verificar_pago"
    const val VerificarPagoWithArg = "verificar_pago/{orderNumber}"

    // Panel administrador
    const val AdminVerificaciones = "admin_verificaciones"
    const val PedidoVerificado = "pedido_verificado"

    // Detalle de pedido (admin)
    const val OrderDetail = "order_detail/{orderNumber}"

    const val EDIT_PRICES_SCREEN = "edit_prices"
    const val VERIFICAR_ORDENES_SCREEN = "admin_verificaciones"
    const val REPORTES_FECHA_SCREEN = "resumen_admin_screen"
    const val ACTUALIZAR_IMAGENES_SCREEN = "actualizar_imagenes"
    const val EditarPagoMovil = "editar_pago_movil"
    const val GESTIONAR_PRODUCTOS_SCREEN = "gestionar_productos"
    const val crearProductoRoute = "crear_producto"
    const val Horario = "horario"
    const val TerminosCondiciones = "terminos_condiciones"
    const val PoliticaPrivacidad = "politica_privacidad"
    const val Ayuda = "ayuda"
    const val EditarPerfil = "editar_perfil"

    fun orderDetailRoute(orderNumber: String) = "order_detail/$orderNumber"

    // Función para navegación a pantalla de verificación de pago
    fun verificarPagoRoute(orderNumber: String): String {
        return "verificar_pago/$orderNumber"
    }

    // Función para navegar a pantalla de estado de pedido con monto total
    fun orderStatusRoute(orderNumber: String, montoTotal: Double): String {
        return "order_status/$orderNumber/$montoTotal"
    }

    object AdminDashboard {
        const val route = "admin_dashboard"
    }
}