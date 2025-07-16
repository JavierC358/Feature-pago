package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.presentation.viewmodel.PagoMovilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPagoMovilScreen(
    viewModel: PagoMovilViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val banco by viewModel.banco.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val cedula by viewModel.cedula.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val isSaving by viewModel.isSaving

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Datos de Pago Móvil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = banco,
                onValueChange = { viewModel.onBancoChange(it) },
                label = { Text("Banco") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = cedula,
                onValueChange = { viewModel.onCedulaChange(it) },
                label = { Text("Cédula o RIF") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre o Razon Social") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.guardarPagoMovil() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}