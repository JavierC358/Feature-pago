package com.example.mordisko.features.admin.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificacionesScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVerificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verificaciones de pago") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
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
                                if (item.status == "pendiente") {
                                    Button(onClick = {
                                        viewModel.marcarComoVerificada(item.orderNumber)
                                    }) {
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
