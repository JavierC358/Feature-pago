package com.example.mordisko.features.admin.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToVerificaciones: () -> Unit,
    onNavigateToResumen: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardRow(
                items = listOf(
                    DashboardItem("Verificar Órdenes", Icons.Default.Check, onNavigateToVerificaciones),
                    DashboardItem("Reportes por Fecha", Icons.Default.DateRange, onNavigateToResumen)
                )
            )
            DashboardRow(
                items = listOf(
                    DashboardItem("Cambiar Precios", Icons.Default.AttachMoney) { /* TODO */ },
                    DashboardItem("Actualizar Imágenes", Icons.Default.Image) { /* TODO */ }
                )
            )
            DashboardRow(
                items = listOf(
                    DashboardItem("Gestionar Menú", Icons.Default.LocalPizza) { /* TODO */ },
                    DashboardItem("Configuraciones", Icons.Default.Settings) { /* TODO */ }
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun DashboardRow(items: List<DashboardItem>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clickable { item.onClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}