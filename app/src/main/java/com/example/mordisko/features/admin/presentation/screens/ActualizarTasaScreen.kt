package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.features.admin.presentation.viewmodel.ExchangeRateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarTasaScreen(
    navController: NavController,
    viewModel: ExchangeRateViewModel = hiltViewModel()
) {
    var nuevaTasa by remember { mutableStateOf("") }
    val tasaActual by viewModel.exchangeRate.collectAsState()

    // Cargar la tasa actual al entrar en pantalla
    LaunchedEffect(Unit) {
        viewModel.getExchangeRate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualizar Tasa de Cambio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tasa actual (USD → Bs): ${tasaActual ?: "Cargando..."}")

            OutlinedTextField(
                value = nuevaTasa,
                onValueChange = { nuevaTasa = it },
                label = { Text("Nueva tasa") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    nuevaTasa.toDoubleOrNull()?.let {
                        viewModel.guardarExchangeRate(it)
                        nuevaTasa = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar tasa")
            }
        }
    }
}