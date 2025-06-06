package com.example.mordisko.features.authentication.presentation.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.R
import com.example.mordisko.ui.theme.Gray

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val navigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(navigationState) {
        when (navigationState) {
            is SplashNavigation.Login -> {
                Log.d("SplashScreen", "Navegando a Login")
                onNavigateToLogin()
            }
            is SplashNavigation.Home -> {
                Log.d("SplashScreen", "Navegando a Home")
                onNavigateToHome()
            }
            else -> {}
        }
    }


    Box(modifier = Modifier
        .padding(horizontal = 24.dp, vertical = 12.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bienvenidos",
                fontSize = 36.sp,
                color = Gray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_burger_background),
                contentDescription = ""
            )
        }
    }
}


