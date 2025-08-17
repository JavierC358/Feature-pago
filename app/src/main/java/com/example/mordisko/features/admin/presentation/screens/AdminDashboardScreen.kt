package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.data.subirProductosAFirestore
import com.example.mordisko.features.admin.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToVerificaciones: () -> Unit,
    onNavigateToResumen: () -> Unit,
    onNavigateToEditPrices: () -> Unit,
    onNavigateToActualizarImagenes: () -> Unit,
    onNavigateToActualizarTasa: () -> Unit,
    onNavigateToEditarDescripcion: () -> Unit,
    onNavigateToEditarPagoMovil: () -> Unit,
    onNavigateToGestionarProductos: () -> Unit,
    onNavigateToCrearProducto: () -> Unit,
    // 🔹 Nuevo: navegación a Gestión de Categorías (tarjeta #10)
    onNavigateToGestionarCategorias: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: AdminViewModel = hiltViewModel()

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
                    DashboardItem("Cambiar Precios", Icons.Default.AttachMoney, onNavigateToEditPrices),
                    DashboardItem("Actualizar Imágenes", Icons.Default.Image, onNavigateToActualizarImagenes),
                )
            )
            DashboardRow(
                items = listOf(
                    DashboardItem("Cambiar Tasa $/Bs", Icons.Default.LocalPizza) { onNavigateToActualizarTasa() },
                    DashboardItem("Gestionar Productos", Icons.Default.Settings) { onNavigateToGestionarProductos() }
                )
            )

            DashboardRow(
                items = listOf(
                    DashboardItem("Editar descripción productos", Icons.Default.AttachMoney, onNavigateToEditarDescripcion),
                    DashboardItem("Editar pago móvil", Icons.Default.Image, onNavigateToEditarPagoMovil)
                )
            )

            DashboardRow(
                items = listOf(
                    DashboardItem("Crear Producto", Icons.Default.LocalPizza, onNavigateToCrearProducto),
                    // 🔹 Reemplazo “Reserva” por “Gestionar Categorías”
                    DashboardItem("Gestionar Categorías", Icons.Default.Settings, onNavigateToGestionarCategorias)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    .height(85.dp)
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
