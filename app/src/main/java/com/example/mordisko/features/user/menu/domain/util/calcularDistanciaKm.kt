package com.example.mordisko.features.user.menu.domain.util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

fun calcularDistanciaKm(userLat: Double, userLng: Double): Double {
    // 📍 Coordenadas fijas de la tienda Mordisko
    val storeLat = 10.123456
    val storeLng = -66.987654

    val tienda = LatLng(storeLat, storeLng)
    val cliente = LatLng(userLat, userLng)

    val distanciaMetros = SphericalUtil.computeDistanceBetween(tienda, cliente)
    return distanciaMetros / 1000 // Retorna la distancia en km
}