package com.example.mordisko.features.admin.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.features.admin.presentation.viewmodel.CrearProductoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProductoScreen(
    onBack: () -> Unit,
    viewModel: CrearProductoViewModel = hiltViewModel()
) {
    val nombre by viewModel.nombre.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val precio by viewModel.precio.collectAsState()
    val imagen by viewModel.imagen.collectAsState()
    val visible by viewModel.visible.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Producto") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = viewModel::onCategoriaChange,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = viewModel::onPrecioChange,
                label = { Text("Precio en USD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imagen,
                onValueChange = viewModel::onImagenChange,
                label = { Text("Nombre del recurso de imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = visible, onCheckedChange = viewModel::onVisibleChange)
                Text("Visible para el cliente")
            }

            val context = LocalContext.current

            Button(
                onClick = {
                    viewModel.guardarProducto(
                        onSuccess = {
                            Toast.makeText(context, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }
        }
    }
}