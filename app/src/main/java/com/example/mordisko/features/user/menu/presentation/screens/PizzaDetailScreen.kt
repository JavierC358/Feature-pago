package com.example.mordisko.features.user.menu.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mordisko.features.user.cart.domain.model.CartItem
import com.example.mordisko.features.user.cart.domain.model.SelectedExtra
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.presentation.viewmodel.MenuViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaDetailScreen(
    pizza: PizzaItem,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel
) {
    LaunchedEffect(Unit) {
        Log.d("PizzaDetailScreen", "cartViewModel hash: ${cartViewModel.hashCode()}")
    }

    val sheetState = rememberModalBottomSheetState()
    var showExtrasSheet by remember { mutableStateOf(false) }

    val selectedExtrasMap by viewModel.selectedExtras.collectAsState()
    val selectedPizza = viewModel.selectedPizza.collectAsState().value
    val selectedExtras = selectedPizza?.name?.let { selectedExtrasMap[it] } ?: emptyList()

    val textColor = Color(0xFFE05B13)
    var selectedSize by remember { mutableStateOf("Med") }
    var quantity by remember { mutableStateOf(1) }
    val sizes = listOf("EG", "Gde", "Med", "Peq")

    val showSizes = pizza.category in listOf(
        PizzaItemCategory.PIZZAS,
        PizzaItemCategory.DEDOS_DE_QUESO,
        PizzaItemCategory.EXTRAS
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()), // ✅ scroll para evitar que el carrito se pierda
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = textColor)
                }
                Text(
                    text = "Volver",
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(start = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            AsyncImage(
                model = pizza.imageUrl,
                contentDescription = pizza.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(pizza.name, style = MaterialTheme.typography.titleLarge, color = textColor)
            Text(pizza.description, style = MaterialTheme.typography.bodyMedium, color = textColor)

            Spacer(modifier = Modifier.height(16.dp))

            if (showSizes) {
                Text("Tamaño:", style = MaterialTheme.typography.titleMedium, color = textColor)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sizes.forEach { size ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = size,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .selectable(
                                        selected = (size == selectedSize),
                                        onClick = { selectedSize = size }
                                    )
                                    .background(
                                        if (size == selectedSize) textColor.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                color = textColor
                            )
                            pizza.priceBySize?.get(size)?.let {
                                Text("$${"%.2f".format(it)}", color = textColor, style = MaterialTheme.typography.labelSmall)
                                Text("Bs ${"%,.2f".format(it * 100)}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                pizza.priceUsd?.let {
                    Text("$${"%.2f".format(it)}", color = textColor, style = MaterialTheme.typography.titleLarge)
                    Text("Bs ${"%,.2f".format(it * 100)}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (pizza.category == PizzaItemCategory.PIZZAS) {
                Button(
                    onClick = { showExtrasSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = textColor)
                ) {
                    Text("Añadir Extra", color = Color.White)
                }
            }

            if (selectedExtras.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Extras añadidos:", style = MaterialTheme.typography.titleMedium, color = textColor)

                selectedExtras.forEach { extra ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("• ${extra.name} (${extra.size})", color = textColor)
                            Text(
                                "$${"%.2f".format(extra.priceUsd)} / Bs ${"%,.2f".format(extra.priceUsd * 36.5)}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = {
                            viewModel.removeExtra(extra.name, extra.size)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar extra", tint = textColor)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar", tint = textColor)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = {
                    val unitPrice = (pizza.priceBySize?.get(selectedSize) ?: pizza.priceUsd ?: 0.0) +
                            selectedExtras.sumOf { it.priceUsd }

                    cartViewModel.addItem(
                        CartItem(
                            name = pizza.name,
                            size = selectedSize,
                            quantity = quantity,
                            imageUrl = pizza.imageUrl,
                            priceUsd = unitPrice,
                            extras = selectedExtras
                        )
                    )

                    viewModel.clearExtras()
                    viewModel.clearSelectedPizza()
                    navController.navigate("cart")
                }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Agregar al carrito", tint = textColor, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar", tint = textColor)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Cantidad: $quantity", style = MaterialTheme.typography.titleLarge, color = textColor)
        }

        if (showExtrasSheet) {
            ModalBottomSheet(onDismissRequest = { showExtrasSheet = false }, sheetState = sheetState) {
                ExtraSelectionSheet(
                    onExtraSelected = {
                        viewModel.addExtra(it)
                        showExtrasSheet = false
                    },
                    textColor = textColor,
                    viewModel = viewModel // ✅ pasamos el ViewModel aquí
                )
            }
        }
    }
}

@Composable
fun ExtraSelectionSheet(
    onExtraSelected: (SelectedExtra) -> Unit,
    textColor: Color,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val extras by viewModel.extras.collectAsState()
    val sizes = listOf("EG", "Gde", "Med", "Peq")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Selecciona un extra", style = MaterialTheme.typography.titleMedium, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))

        extras.forEach { extra ->
            var selectedSize by remember { mutableStateOf("Med") }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Text(extra.name, style = MaterialTheme.typography.labelMedium, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    sizes.forEach { size ->
                        val price = extra.priceBySize?.get(size) ?: 0.0
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = size,
                                modifier = Modifier
                                    .clickable { selectedSize = size }
                                    .background(if (selectedSize == size) textColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .padding(4.dp),
                                color = textColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text("$${"%.2f".format(price)}", color = textColor, style = MaterialTheme.typography.labelSmall)
                            Text("Bs ${"%,.2f".format(price * 36.5)}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = {
                        val price = extra.priceBySize?.get(selectedSize) ?: 0.0
                        onExtraSelected(
                            SelectedExtra(
                                name = extra.name ?: "Extra",
                                size = selectedSize,
                                priceUsd = price
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = textColor),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Agregar", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}