package com.example.mordisko.features.user.menu.presentation.screens

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Opciones de peso (clave usada en Firestore -> etiqueta visible)
    val WEIGHT_OPTIONS = listOf(
        "250" to "0.250 Kg",
        "500" to "0.500 Kg",
        "750" to "0.750 Kg",
        "1000" to "1.00 Kg"
    )

    val BROASTER_PORTIONS = listOf(
        "QUARTER" to "1/4 Pollo",
        "HALF" to "1/2 Pollo",
        "THREE_QUARTERS" to "3/4 Pollo",
        "WHOLE" to "1 pollo"
    )

    val exchangeRate by viewModel.exchangeRate.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var showExtrasSheet by remember { mutableStateOf(false) }

    val selectedExtrasMap by viewModel.selectedExtras.collectAsState()
    val selectedPizza = viewModel.selectedPizza.collectAsState().value
    val selectedExtras = selectedPizza?.name?.let { selectedExtrasMap[it] } ?: emptyList()

    val textColor = Color(0xFFE05B13)

    // Tamaños de pizza
    var selectedSize by remember { mutableStateOf("Med") }
    val sizes = listOf("EG", "Gde", "Med", "Peq")
    val showSizes = pizza.category in listOf(PizzaItemCategory.PIZZAS, PizzaItemCategory.EXTRAS)

    // Pesos para carne / ahumados
    val isMeatCategory =
        pizza.category == PizzaItemCategory.CARNE_EN_VARA || pizza.category == PizzaItemCategory.AHUMADOS
    val isBroasterCategory = pizza.category == PizzaItemCategory.A_LA_BROASTER
    var selectedWeightKey by remember { mutableStateOf("500") } // default 0.500 Kg
    var selectedPortionKey by remember { mutableStateOf("HALF") }

    var quantity by remember { mutableStateOf(1) }

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
                .verticalScroll(rememberScrollState()),
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
            Text(
                pizza.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ====== BLOQUE NUEVO: selector por peso para Carne en Vara / Ahumados ======
            if (isMeatCategory) {
                Text("Presentación:", style = MaterialTheme.typography.titleMedium, color = textColor)
                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WEIGHT_OPTIONS.forEach { (key, label) ->
                        val usd = pizza.priceByWeight?.get(key)
                        WeightOptionCard(
                            label = label,
                            selected = key == selectedWeightKey,
                            onClick = { if (usd != null) selectedWeightKey = key },
                            usd = usd,
                            bs = usd?.let { it * exchangeRate },
                            accent = textColor,
                            labelFontSize = 11.sp,          // 👈 ajusta aquí
                            priceFontSizeUsd = 11.sp,
                            priceFontSizeBs = 10.sp,
                            horizontalPad = 8,
                            verticalPad = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            // ====== FIN BLOQUE NUEVO ======

            // ====== BLOQUE NUEVO: selector por porción para A la Broaster ======
            if (isBroasterCategory) {
                Text("Porción:", style = MaterialTheme.typography.titleMedium, color = textColor)
                Spacer(Modifier.height(8.dp))

                // Puedes usar FlowRow si quieres que se ajuste mejor:
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BROASTER_PORTIONS.forEach { (key, label) ->
                        val usd = pizza.priceByPortion[key]
                        WeightOptionCard( // reutilizamos el mismo componente; si quieres, renómbralo a OptionCard
                            label = label,
                            selected = key == selectedPortionKey,
                            onClick = { if (usd != null) selectedPortionKey = key },
                            usd = usd,
                            bs = usd?.let { it * exchangeRate },
                            accent = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
// ====== FIN BLOQUE NUEVO ======

            // Tamaños de pizza (se muestra solo si NO es carne/ahumados)
            if (!isMeatCategory && showSizes) {
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
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            val sizeInCm = when (size) {
                                "EG" -> "42 cm"
                                "Gde" -> "36  cm"
                                "Med" -> "32 cm"
                                "Peq" -> "22 cm"
                                else -> ""
                            }
                            if (sizeInCm.isNotEmpty()) {
                                Text(sizeInCm, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }

                            pizza.priceBySize?.get(size)?.let {
                                Text(
                                    "$${"%.2f".format(it)}",
                                    color = textColor,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Bs ${"%,.2f".format(it * exchangeRate)}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (!isMeatCategory) {
                // Items sin tamaños (ni peso)
                pizza.priceUsd?.let {
                    Text("$${"%.2f".format(it)}", color = textColor, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Bs ${"%,.2f".format(it * exchangeRate)}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
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
                                "$${"%.2f".format(extra.priceUsd)} / Bs ${"%,.2f".format(extra.priceUsd * exchangeRate)}",
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
                    val baseUsd = when {
                        isMeatCategory -> pizza.priceByWeight[selectedWeightKey] ?: 0.0
                        isBroasterCategory -> pizza.priceByPortion[selectedPortionKey] ?: 0.0   // 👈 NUEVO
                        else -> (pizza.priceBySize[selectedSize] ?: pizza.priceUsd ?: 0.0)
                    }

                    val unitPrice = baseUsd + selectedExtras.sumOf { it.priceUsd }

                    val sizeOrWeightOrPortionLabel = when {
                        isMeatCategory -> WEIGHT_OPTIONS.firstOrNull { it.first == selectedWeightKey }?.second ?: "0.500 Kg"
                        isBroasterCategory -> BROASTER_PORTIONS.firstOrNull { it.first == selectedPortionKey }?.second ?: "1/2 Pollo" // 👈 NUEVO
                        else -> selectedSize
                    }

                    cartViewModel.addItem(
                        CartItem(
                            name = pizza.name,
                            size = sizeOrWeightOrPortionLabel,  // 👈 enviamos la porción seleccionada como “size”
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
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        tint = textColor,
                        modifier = Modifier.size(36.dp)
                    )
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
                    viewModel = viewModel,
                    exchangeRate = exchangeRate
                )
            }
        }
    }
}

@Composable
private fun WeightOptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    usd: Double?,
    bs: Double?,
    accent: Color,
    labelFontSize: androidx.compose.ui.unit.TextUnit = 11.sp,  // 👈 más pequeño
    priceFontSizeUsd: androidx.compose.ui.unit.TextUnit = 11.sp,
    priceFontSizeBs: androidx.compose.ui.unit.TextUnit = 10.sp,
    horizontalPad: Int = 8,  // 👈 menos padding
    verticalPad: Int = 1
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            modifier = Modifier
                .padding(4.dp)
                .selectable(selected = selected, onClick = onClick)
                .background(if (selected) accent.copy(alpha = 0.2f) else Color.Transparent)
                .padding(horizontal = horizontalPad.dp, vertical = verticalPad.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = labelFontSize,
            maxLines = 1
        )
        if (usd != null) {
            Text("$${"%.2f".format(usd)}", color = accent, fontSize = priceFontSizeUsd, maxLines = 1)
            if (bs != null) {
                Text("Bs ${"%,.2f".format(bs)}", color = Color.Gray, fontSize = priceFontSizeBs, maxLines = 1)
            }
        } else {
            Text("-", color = Color.Gray, fontSize = priceFontSizeBs, maxLines = 1)
        }
    }
}

@Composable
fun ExtraSelectionSheet(
    onExtraSelected: (SelectedExtra) -> Unit,
    textColor: Color,
    viewModel: MenuViewModel = hiltViewModel(),
    exchangeRate: Double
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
            var selectedSize by remember { mutableStateOf("EG") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    extra.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    sizes.forEach { size ->
                        val price = extra.priceBySize?.get(size) ?: 0.0
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = size,
                                modifier = Modifier
                                    .clickable { selectedSize = size }
                                    .background(if (selectedSize == size) textColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .padding(4.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text("$${"%.2f".format(price)}", color = textColor, style = MaterialTheme.typography.labelSmall)
                            Text(
                                "Bs ${"%,.2f".format(price * exchangeRate)}",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
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