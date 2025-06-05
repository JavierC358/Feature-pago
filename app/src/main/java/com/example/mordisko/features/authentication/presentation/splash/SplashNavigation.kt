package com.example.mordisko.features.authentication.presentation.splash

sealed class SplashNavigation {
    object Loading : SplashNavigation()
    object Login : SplashNavigation()
    object Home : SplashNavigation()

}