package com.example.mordisko.features.user.cart.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mordisko.ui.theme.orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    navController: NavController,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    viewModel: CartViewModel
) {
    LaunchedEffect(Unit) {
        Log.d("CartScreen", "CartViewModel instancia: ${cartViewModel.hashCode()}")
    }

    val items by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current

    val cartItems = cartViewModel.cartItems.collectAsState().value
    val textColor = Color.Gray
    val priceColor = Color(0xFFE05B13)
    val subtotal = cartItems.sumOf { it.priceUsd * it.quantity}

    var showCommentDialog by remember { mutableStateOf(false) }
    val commentState = cartViewModel.orderComment.collectAsState()
    var comment by remember { mutableStateOf(commentState.value) }

    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            confirmButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Aceptar")
                }

                cartViewModel.setOrderComment(comment)
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Agrega un comentario") },
            text = {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
                    ) {
                        TextField(
                            value = comment,
                            onValueChange = { if (it.length <= 160) comment = it },
                            placeholder = { Text("Agrega tu comentario", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${comment.length}/160",
                        modifier = Modifier.align(Alignment.End),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = priceColor)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Volver", color = priceColor, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${cartItems.size} producto(s) seleccionados",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                                // Cantidad en círculo
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(color = priceColor, shape = MaterialTheme.shapes.small),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.quantity.toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            Text(
                                text = "$${"%.2f".format(item.priceUsd)}",
                                color = priceColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text("Tamaño: ${item.size}", color = textColor, fontSize = 12.sp)

                            // ==== Extras con basurero por cada uno (cambio puntual) ====
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                item.extras.forEach { extra ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "• ${extra.name} (${extra.size})",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { cartViewModel.removeExtra(item, extra) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Quitar extra",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            // ============================================================
                        }

                        IconButton(onClick = { cartViewModel.removeItem(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = textColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comentario clickable centrado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showCommentDialog = true },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar nota", tint = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agrega un comentario a tu pedido",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", color = textColor, style = MaterialTheme.typography.bodyLarge)
            Text("$${"%.2f".format(subtotal)}", color = priceColor, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (items.isNotEmpty()) {
                    onContinue()
                } else {
                    Toast.makeText(context, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = items.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orange)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}