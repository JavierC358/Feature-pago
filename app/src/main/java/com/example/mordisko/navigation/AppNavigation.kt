package com.example.mordisko.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mordisko.features.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.features.authentication.presentation.login.ForgotPasswordScreen
import com.example.mordisko.features.authentication.presentation.login.LoginScreen
import com.example.mordisko.features.authentication.presentation.login.RegisterScreen
import com.example.mordisko.features.home.HomeScreen
import com.example.mordisko.features.authentication.presentation.splash.SplashScreen
import com.example.mordisko.features.menu.navigation.menuNavGraph // ✅ AÑADIR ESTA IMPORTACIÓN

@Composable
fun AppNavigation(
    navController: NavHostController,
    googleLauncher: ActivityResultLauncher<Intent>,
    googleAuthViewModel: GoogleAuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                googleLauncher = googleLauncher,
                googleAuthViewModel = googleAuthViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onCategorySelected = { category ->
                    navController.navigate("menu/$category")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Menu y detalle de pizza (importante usar this)
        this.menuNavGraph(navController)
    }
}

