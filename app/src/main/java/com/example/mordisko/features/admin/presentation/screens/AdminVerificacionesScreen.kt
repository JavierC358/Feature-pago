package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mordisko.core.navigation.Routes
import com.example.mordisko.features.admin.presentation.viewmodel.AdminViewModel
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificacionesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVerificaciones() // carga página 1 con pageSize actual
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificaciones de pago", color = orange) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("admin_dashboard") {
                            popUpTo(Routes.AdminVerificaciones) { inclusive = true }
                        }
                    }) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = orange) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading && state.verificaciones.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = orange)
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
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = orange)
                                    ) { Text("Ver Detalles") }

                                    if (item.status.lowercase() != "verificado") {
                                        Button(
                                            onClick = { viewModel.marcarComoVerificada(item.orderNumber) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = orange)
                                        ) { Text("Marcar como verificada") }
                                    }
                                }
                            }
                        }
                    }

                    // Loader de pie cuando cambia de página
                    if (state.isLoading && state.verificaciones.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = orange)
                            }
                        }
                    }
                }

                Divider(color = lightOrange.copy(alpha = 0.6f))

                PaginationBar(
                    pageSize = state.pageSize,
                    onPageSizeChange = { viewModel.onChangePageSize(it) },
                    currentPage = state.currentPage,
                    totalPages = state.totalPages,
                    onPrev = { viewModel.prevPage() },
                    onNext = { viewModel.nextPage() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaginationBar(
    pageSize: Int,
    onPageSizeChange: (Int) -> Unit,
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(5, 10, 20)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Selector "Reg..."
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = "Reg…  $pageSize",
                onValueChange = {},
                label = { Text("Reg…") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .width(130.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = orange,
                    unfocusedBorderColor = lightOrange,
                    focusedLabelColor = orange,
                    cursorColor = orange
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt.toString()) },
                        onClick = {
                            onPageSizeChange(opt) // reinicia a página 1 internamente
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(text = "$currentPage / $totalPages", style = MaterialTheme.typography.bodyLarge)

        Row {
            IconButton(onClick = onPrev, enabled = currentPage > 1) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior",
                    tint = if (currentPage > 1) orange else LocalContentColor.current.copy(alpha = 0.38f))
            }
            IconButton(onClick = onNext, enabled = currentPage < totalPages) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente",
                    tint = if (currentPage < totalPages) orange else LocalContentColor.current.copy(alpha = 0.38f))
            }
        }
    }
}
