package com.example.mordisko.features.user.profile.domain.model

data class UserProfile(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val nacionalidad: String = "",
    val cedula: String = "",
    val photoUrl: String = ""
)