package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.presentation.viewmodel.GestionarProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarProductosScreen(
    viewModel: GestionarProductosViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestionar Productos") })
        }
    ) { padding ->

        Button(
            onClick = { /* navegar a CrearProductoScreen */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear Producto")
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(productos) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.elevatedCardElevation(4.dp) // corregido
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = producto.name, fontWeight = FontWeight.Bold)
                        Text(text = "Categoría: ${producto.category}")
                        Text(text = "Visible: ${producto.visible}")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                viewModel.cambiarVisibilidad(producto.id, !producto.visible)
                            }) {
                                Text(if (producto.visible) "Ocultar" else "Mostrar")
                            }

                            Button(
                                onClick = { viewModel.eliminarProducto(producto.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}