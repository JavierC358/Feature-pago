package com.example.mordisko.features.user.history.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.history.domain.model.OrderHistoryItem
import com.example.mordisko.features.user.history.presentation.viewmodel.OrderHistoryViewModel
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: OrderHistoryViewModel,
    cartViewModel: CartViewModel,
    onRepetirPedido: () -> Unit,
    onBack: () -> Unit
) {
    val pedidos by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos", color = orange, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = orange)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Contenido
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when {
                    isLoading && pedidos.isEmpty() -> CircularProgressIndicator(color = orange)
                    errorMessage != null -> Text(errorMessage ?: "", color = Color.Red, fontSize = 16.sp)
                    pedidos.isEmpty() -> Text("No tienes pedidos registrados.", color = Color.Gray, fontSize = 16.sp)
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxSize()
                        ) {
                            items(
                                items = pedidos,
                                key = { "${it.orderNumber}-${it.timestamp}" }
                            ) { pedido ->
                                OrderCard(
                                    pedido = pedido,
                                    onRepetir = {
                                        cartViewModel.repetirPedido(pedido.items)
                                        onRepetirPedido()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Barra de paginación
            Divider(color = lightOrange.copy(alpha = 0.6f))
            PaginationBar(
                pageSize = pageSize,
                onPageSizeChange = { viewModel.onChangePageSize(it) },
                currentPage = currentPage,
                totalPages = totalPages,
                onPrev = { viewModel.prevPage() },
                onNext = { viewModel.nextPage() }
            )
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // (Opcional) Botón de exportar/descargar a la izquierda
        IconButton(onClick = { /* TODO: exportar */ }) {
            Icon(Icons.Default.Download, contentDescription = "Descargar", tint = orange)
        }

        // Selector "Reg…" (page size)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = "Reg…  $pageSize",
                onValueChange = {},
                label = { Text("Reg…") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().width(130.dp),
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
                            onPageSizeChange(opt)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Indicador "página / total"
        Text(
            text = "$currentPage / $totalPages",
            style = MaterialTheme.typography.bodyLarge
        )

        // Flechas
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPrev,
                enabled = currentPage > 1
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = if (currentPage > 1) orange else Color.Gray)
            }
            IconButton(
                onClick = onNext,
                enabled = currentPage < totalPages
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = if (currentPage < totalPages) orange else Color.Gray)
            }
        }
    }
}

// Tu tarjeta original con colores
@Composable
private fun OrderCard(
    pedido: OrderHistoryItem,
    onRepetir: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = lightOrange),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Orden #${pedido.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verificado",
                    tint = if (pedido.paymentStatus == "verificado") Color.Green else Color.Gray
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${pedido.formattedDate()}")
            Text("Total: ${pedido.totalUsd} USD / ${pedido.totalBs} Bs")
            Text("Método de pago: ${pedido.paymentMethod}")
            Text("Estado: ${pedido.paymentStatus}")
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRepetir, colors = ButtonDefaults.buttonColors(containerColor = orange)) {
                Text("Repetir pedido", color = Color.White)
            }
        }
    }
}