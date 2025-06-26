package com.example.mordisko.features.user.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.mordisko.features.user.authentication.presentation.google.GoogleAuthViewModel
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.navigation.AppNavigation
import com.example.mordisko.ui.theme.MordiskoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthViewModel: GoogleAuthViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels() // ✅ ViewModel compartido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (data != null) {
                googleAuthViewModel.handleGoogleSignInResult(data, this)
            } else {
                googleAuthViewModel.setError("No se recibió respuesta del intento de inicio con Google.")
            }
        }

        setContent {
            MordiskoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        googleLauncher = googleLauncher,
                        googleAuthViewModel = googleAuthViewModel,
                        cartViewModel = cartViewModel // ✅ ViewModel compartido en navegación
                    )
                }
            }
        }
    }
}