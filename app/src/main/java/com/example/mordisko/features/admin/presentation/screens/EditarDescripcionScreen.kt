package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.features.admin.presentation.viewmodel.EditarDescripcionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarDescripcionScreen(
    navController: NavController,
    viewModel: EditarDescripcionViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar descripción") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productos) { producto ->
                var descripcion by remember { mutableStateOf(producto.descripcion ?: "") }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(producto.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.actualizarDescripcion(producto.id, descripcion)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}