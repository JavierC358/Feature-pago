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
import com.example.mordisko.features.admin.presentation.viewmodel.OrdersFilter
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificacionesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ){ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading && state.verificaciones.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = orange)
            } else {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.filter == OrdersFilter.POR_VERIFICAR,
                        onClick = { viewModel.setFilter(OrdersFilter.POR_VERIFICAR) },
                        label = { Text("Por verificar") }
                    )
                    FilterChip(
                        selected = state.filter == OrdersFilter.TODAS,
                        onClick = { viewModel.setFilter(OrdersFilter.TODAS) },
                        label = { Text("Historial") }
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {

                    items(state.verificaciones) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Orden: ${order.orderNumber}")
                                val totalTexto = order.totalBs?.let { String.format("%.2f", it) } ?: "--"
                                val pagoTexto = order.amountPaid?.let { String.format("%.2f", it) } ?: "--"

                                Text("Monto (Total): Bs $totalTexto")
                                Text("Pagó (Pago móvil): Bs $pagoTexto")
                                Text("Ref: ${order.referenceLast4}")
                                val phoneRaw = order.phoneNumber?.trim().orEmpty()
                                val phone = phoneRaw.replace(" ", "")
                                val phoneDisponible = phone.isNotBlank() && phone != "--"

                                Row {
                                    Text("Tel: ")
                                    Text(
                                        text = if (phoneDisponible) phone else "--",
                                        modifier = if (phoneDisponible) {
                                            Modifier.clickable {
                                                clipboardManager.setText(AnnotatedString(phone))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Copiado: $phone")
                                                }
                                            }
                                        } else {
                                            Modifier
                                        },
                                        color = if (phoneDisponible) orange else LocalContentColor.current,
                                        textDecoration = if (phoneDisponible) TextDecoration.Underline else null
                                    )
                                }
                                val estadoMostrado = if (order.orderStatus.trim().lowercase() == "completado") {
                                    "completado"
                                } else {
                                    order.status
                                }

                                Text("Estado: $estadoMostrado")

                                // ✅ NUEVO: estado de despacho (para que el admin vea si ya está despachada)
                                val despacho = order.orderStatus.lowercase()

                                val despachoTexto = when (despacho) {
                                    "completado" -> "Despacho: ✅ Completado"
                                    "cancelado" -> "Despacho: ❌ Cancelado"
                                    else -> "Despacho: ⏳ Pendiente"
                                }

                                Text(
                                    text = despachoTexto,
                                    color = when (despacho) {
                                        "completado" -> orange
                                        "cancelado" -> Color.Red
                                        else -> LocalContentColor.current
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { navController.navigate("order_detail/${order.orderNumber}") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = orange)
                                    ) { Text("Ver Detalles") }

                                    // ✅ Tu botón actual: solo para pago móvil por verificar
                                    val esPagoMovil = order.amountPaid != null || order.referenceLast4 != "--" || order.phoneNumber != "--"

                                    if (
                                        state.filter == OrdersFilter.POR_VERIFICAR &&
                                        order.status.lowercase() != "verificado" &&
                                        esPagoMovil
                                    ) {
                                        Button(
                                            onClick = { viewModel.marcarComoVerificada(order.orderNumber) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = orange)
                                        ) { Text("Marcar como verificada") }
                                    }
                                }

                                // ✅ NUEVO: botón COMPLETAR (para efectivo / punto / y también móviles ya despachados)
                                // Lo mostramos solo si aún NO está completado
                                if (despacho != "completado" && despacho != "cancelado") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.completarOrden(order.orderNumber) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = orange)
                                    ) {
                                        Text("Completar (Despachada)")
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
