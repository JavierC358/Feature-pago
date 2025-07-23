package com.example.mordisko.features.user.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    viewModel: EditarPerfilViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGuardarExitoso: () -> Unit,
    pickImage: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val nombreError by viewModel.nombreError.collectAsState()
    val correoError by viewModel.correoError.collectAsState()
    val telefonoError by viewModel.telefonoError.collectAsState()
    val cedulaError by viewModel.cedulaError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    var nacionalidadDropdownExpanded by remember { mutableStateOf(false) }
    val nacionalidades = listOf("V", "E", "J")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar datos personales") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil
            Box(contentAlignment = Alignment.BottomEnd) {
                val displayImage = imageUri ?: profile.photoUrl.takeIf { it.isNotBlank() }

                if (displayImage != null) {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }

                IconButton(
                    onClick = pickImage,
                    modifier = Modifier
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(32.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = profile.nombre,
                onValueChange = { viewModel.onProfileChange(profile.copy(nombre = it)) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
                supportingText = { nombreError?.let { Text(it, color = Color.Red) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = profile.correo,
                onValueChange = { viewModel.onProfileChange(profile.copy(correo = it)) },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                isError = correoError != null,
                supportingText = { correoError?.let { Text(it, color = Color.Red) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = profile.telefono,
                onValueChange = { viewModel.onProfileChange(profile.copy(telefono = it)) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                isError = telefonoError != null,
                supportingText = { telefonoError?.let { Text(it, color = Color.Red) } },
                placeholder = { Text("Ej: 04141234567") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { nacionalidadDropdownExpanded = true }
                ) {
                    Column {
                        Text("Nacionalidad", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(Color.White, shape = MaterialTheme.shapes.small)
                                .padding(12.dp)
                        ) {
                            Text(text = profile.nacionalidad.ifBlank { "Seleccionar..." })
                        }

                        DropdownMenu(
                            expanded = nacionalidadDropdownExpanded,
                            onDismissRequest = { nacionalidadDropdownExpanded = false }
                        ) {
                            nacionalidades.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        viewModel.onProfileChange(profile.copy(nacionalidad = opcion))
                                        nacionalidadDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = profile.cedula,
                    onValueChange = { viewModel.onProfileChange(profile.copy(cedula = it)) },
                    label = { Text("Cédula") },
                    modifier = Modifier.weight(2f),
                    isError = cedulaError != null,
                    supportingText = { cedulaError?.let { Text(it, color = Color.Red) } },
                    placeholder = { Text("Ej: 12345678") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveProfile {
                        onGuardarExitoso()
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Guardar cambios",
                    fontWeight = FontWeight.Bold
                )
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
