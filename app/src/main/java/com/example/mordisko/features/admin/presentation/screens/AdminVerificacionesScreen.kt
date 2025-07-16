package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.features.admin.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificacionesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVerificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificaciones de pago") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("admin_dashboard") {
                            popUpTo(Routes.AdminVerificaciones) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.verificaciones) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Orden: ${item.orderNumber}")
                                Text("Monto: Bs ${item.amountPaid}")
                                Text("Ref: ${item.referenceLast4}")
                                Text("Tel: ${item.phoneNumber}")
                                Text("Estado: ${item.status}")
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            navController.navigate("order_detail/${item.orderNumber}")
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Ver Detalles")
                                    }

                                    // ✅ Condición robusta para mostrar el botón
                                    if (item.status.lowercase().contains("pendiente")) {
                                        Button(
                                            onClick = {
                                                viewModel.marcarComoVerificada(item.orderNumber)
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Marcar como verificada")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
