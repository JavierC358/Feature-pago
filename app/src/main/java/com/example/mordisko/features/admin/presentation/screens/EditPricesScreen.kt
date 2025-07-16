package com.example.mordisko.features.admin.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.presentation.viewmodel.AdminMenuViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPricesScreen(
    viewModel: AdminMenuViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getAllProductsFromFirestore()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Precios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {

            if (products.isEmpty()) {
                item {
                    Text(
                        text = "No hay productos para mostrar.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(products) { product ->
                ProductEditCard(product = product) { updatedProduct ->
                    viewModel.updateProductPrices(updatedProduct) {
                        Toast.makeText(context, "Precios actualizados", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun ProductEditCard(
    product: PizzaItem,
    onPriceUpdate: (PizzaItem) -> Unit
) {
    val isMultiSize = product.category in listOf(
        PizzaItemCategory.PIZZAS,
        PizzaItemCategory.DEDOS_DE_QUESO,
        PizzaItemCategory.EXTRAS
    )

    var priceBs by remember { mutableStateOf(product.priceBs.toString()) }

    val sizes = listOf("EG", "Gde", "Med", "Peq")
    val prices = remember { mutableStateMapOf<String, String>() }
    var singlePrice by remember { mutableStateOf(product.priceUsd.toString()) }

    // Inicializa los precios por tamaño si aplica
    if (isMultiSize) {
        sizes.forEach { size ->
            val initialPrice = product.priceBySize[size]?.toString() ?: ""
            if (!prices.containsKey(size)) prices[size] = initialPrice
        }
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isMultiSize) {
                sizes.forEach { size ->
                    OutlinedTextField(
                        value = prices[size] ?: "",
                        onValueChange = { prices[size] = it },
                        label = { Text("Precio $size (USD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                OutlinedTextField(
                    value = singlePrice,
                    onValueChange = { singlePrice = it },
                    label = { Text("Precio USD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = priceBs,
                onValueChange = { priceBs = it },
                label = { Text("Precio Bs") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val updated = if (isMultiSize) {
                        val updatedPriceBySize = sizes.associateWith { size ->
                            prices[size]?.toDoubleOrNull() ?: 0.0
                        }
                        product.copy(
                            priceBySize = updatedPriceBySize,
                            priceBs = priceBs.toDoubleOrNull() ?: 0.0
                        )
                    } else {
                        product.copy(
                            priceUsd = singlePrice.toDoubleOrNull() ?: 0.0,
                            priceBs = priceBs.toDoubleOrNull() ?: 0.0
                        )
                    }

                    onPriceUpdate(updated)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}