package com.example.mordisko.features.user.authentication.presentation.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.R

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHorario: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAdminPanel: () -> Unit // ✅ Se mantiene
) {
    val navigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(navigationState) {
        when (navigationState) {
            is SplashNavigation.Login -> {
                Log.d("SplashScreen", "Navegando a Login")
                onNavigateToLogin()
            }
            is SplashNavigation.Horario -> {
                Log.d("NAV_TEST", "Entrando al bloque Horario en SplashScreen")
                onNavigateToHorario()
            }

            is SplashNavigation.Home -> {
                Log.d("SplashScreen", "Navegando a Home")
                onNavigateToHome()
            }

            is SplashNavigation.AdminPanel -> {
                Log.d("SplashScreen", "Navegando a Panel Admin")
                onNavigateToAdminPanel() // ✅ Cambia la ruta cuando la uses en AppNavigation
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo3),
                contentDescription = "",
                Modifier.size(220.dp)
            )
        }
    }
}