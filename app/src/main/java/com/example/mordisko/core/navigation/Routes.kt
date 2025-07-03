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
    fun orderDetailRoute(orderNumber: String) = "order_detail/$orderNumber"

    // Función para navegación a pantalla de verificación de pago
    fun verificarPagoRoute(orderNumber: String): String {
        return "verificar_pago/$orderNumber"
    }

    // Función para navegar a pantalla de estado de pedido con monto total
    fun orderStatusRoute(orderNumber: String, montoTotal: Double): String {
        return "order_status/$orderNumber/$montoTotal"
    }
}