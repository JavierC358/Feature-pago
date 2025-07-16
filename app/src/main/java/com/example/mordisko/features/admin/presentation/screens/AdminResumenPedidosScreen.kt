package com.example.mordisko.features.admin.presentation.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResumenPedidosScreen(
    onConsultarClick: (Date, Date) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var fechaDesde by remember { mutableStateOf<Date?>(null) }
    var fechaHasta by remember { mutableStateOf<Date?>(null) }

    val showDatePicker: (Date?, (Date) -> Unit) -> Unit = { initialDate, onDateSelected ->
        val calendar = Calendar.getInstance().apply {
            time = initialDate ?: Date()
        }
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                onDateSelected(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Órdenes") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                showDatePicker(fechaDesde) { fechaDesde = it }
            }) {
                Text("Seleccionar fecha desde: ${fechaDesde?.let { dateFormat.format(it) } ?: "No seleccionada"}")
            }

            Button(onClick = {
                showDatePicker(fechaHasta) { fechaHasta = it }
            }) {
                Text("Seleccionar fecha hasta: ${fechaHasta?.let { dateFormat.format(it) } ?: "No seleccionada"}")
            }

            Button(
                onClick = {
                    if (fechaDesde != null && fechaHasta != null) {
                        onConsultarClick(fechaDesde!!, fechaHasta!!)
                    }
                },
                enabled = fechaDesde != null && fechaHasta != null
            ) {
                Text("Consultar")
            }
        }
    }
}
