package com.example.mordisko.features.user.history.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mordisko.features.user.history.domain.model.OrderHistoryItem
import com.example.mordisko.features.user.history.presentation.viewmodel.OrderHistoryViewModel
import com.example.mordisko.ui.theme.lightOrange
import com.example.mordisko.ui.theme.orange
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel()
) {
    val orderList by viewModel.orders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Pedidos",
            style = MaterialTheme.typography.headlineSmall,
            color = orange,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orderList) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderHistoryItem) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(order.timestamp)

    Card(
        colors = CardDefaults.cardColors(containerColor = lightOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = "Icono de pedido",
                tint = orange,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Pedido #${order.orderNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = "Total: $${"%.2f".format(order.totalUsd)} / Bs. ${"%.2f".format(order.totalBs ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Estado: ${order.paymentStatus}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (order.paymentStatus == "verificado") Color.Green else Color.Red
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
