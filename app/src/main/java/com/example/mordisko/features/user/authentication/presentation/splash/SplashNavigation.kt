package com.example.mordisko.features.user.authentication.presentation.splash

sealed class SplashNavigation {
    object Loading : SplashNavigation()
    object Login : SplashNavigation()
    object Horario : SplashNavigation()
    object Home : SplashNavigation()

    object AdminPanel : SplashNavigation()

}