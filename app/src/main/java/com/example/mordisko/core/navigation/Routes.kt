package com.example.mordisko.core.navigation

object Routes {
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

    // Ruta dinámica para verificación de pago
    const val VerificarPago = "verificar_pago"
    const val VerificarPagoWithArg = "verificar_pago/{orderNumber}"
    const val AdminVerificaciones = "admin_verificaciones"

    fun verificarPagoRoute(orderNumber: String): String {
        return "verificar_pago/$orderNumber"
    }

    fun orderStatusRoute(orderNumber: String, montoTotal: Double): String {
        return "order_status/$orderNumber/$montoTotal"
    }
}