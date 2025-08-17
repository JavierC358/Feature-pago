package com.example.mordisko.features.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mordisko.features.user.menu.domain.model.PizzaCategory
import com.example.mordisko.features.user.menu.domain.model.PizzaItemCategory
import com.example.mordisko.features.user.menu.domain.model.pizzaCategories
import com.example.mordisko.features.user.menu.domain.model.valueOfOrNull
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategorySelected: (String) -> Unit,
    onBackToHorario: () -> Unit
) {
    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)

    // Firestore categories visibles y ordenadas por position
    data class UiCategory(val name: String, val imageUrl: String)

    var fsCategories by remember { mutableStateOf<List<UiCategory>>(emptyList()) }
    var fsLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("categories")
            .whereEqualTo("visible", true)
            .orderBy("position", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { d ->
                    val name = d.getString("name") ?: return@mapNotNull null
                    val imageUrl = d.getString("imageUrl") ?: ""
                    UiCategory(name, imageUrl)
                } ?: emptyList()
                fsCategories = list
                fsLoaded = true
            }
    }

    val useFirestore = fsLoaded && fsCategories.isNotEmpty()

    // Mantener tu layout circular: centro “Pizzas” si existe
    val centerCategory: PizzaCategory
    val otherCategories: List<PizzaCategory>

    if (useFirestore) {
        val pizzasFirst = fsCategories.firstOrNull { it.name.equals("pizzas", ignoreCase = true) }
        val center = pizzasFirst ?: fsCategories.first()

        fun toPseudoPizzaCategory(c: UiCategory) = PizzaCategory(
            name = c.name,
            imageRes = 0, // no se usa cuando hay URL
            enum = PizzaItemCategory.valueOfOrNull(c.name) ?: PizzaItemCategory.PIZZAS
        )

        centerCategory = toPseudoPizzaCategory(center)
        otherCategories = fsCategories
            .filter { it.name != center.name }
            .map { toPseudoPizzaCategory(it) }
    } else {
        centerCategory = pizzaCategories.firstOrNull { it.name.lowercase() == "pizzas" }
            ?: pizzaCategories.first()
        otherCategories = pizzaCategories.filterNot { it == centerCategory }
    }

    val radius = 130.dp
    val density = LocalDensity.current

    val backgroundGradient = Brush.verticalGradient(colors = listOf(orange, lightOrange))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToHorario) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text("¡Nuestro Menu!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Centro (texto abajo)
            CircularCategoryCard(
                category = centerCategory,
                imageUrl = if (useFirestore) {
                    fsCategories.firstOrNull { it.name.equals(centerCategory.name, true) }?.imageUrl
                        ?.takeUnless { it.isBlank() }
                } else null,
                onClick = { onCategorySelected(centerCategory.enum.name) },
            )

            // Alrededor
            val othersForAngles =
                if (useFirestore) fsCategories.filter { !it.name.equals(centerCategory.name, true) }.map { it.name }
                else otherCategories.map { it.name }

            otherCategories.forEachIndexed { index, category ->
                val angleDeg = (360f / otherCategories.size.coerceAtLeast(1)) * index - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val radiusPx = with(density) { radius.toPx() }
                val offsetX = radiusPx * cos(angleRad)
                val offsetY = radiusPx * sin(angleRad)

                val url: String? = if (useFirestore) {
                    val name = othersForAngles.getOrNull(index) ?: category.name
                    fsCategories.firstOrNull { it.name.equals(name, true) }?.imageUrl
                        ?.takeUnless { it.isBlank() }
                } else null

                val labelOnTop = angleDeg < 0f  // tres superiores: texto arriba

                Box(modifier = Modifier.offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }) {
                    CircularCategoryCard(
                        category = category,  // ya no hacemos copy
                        imageUrl = url,
                        onClick = { onCategorySelected(category.enum.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun CircularCategoryCard(
    category: PizzaCategory,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen centrada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        !imageUrl.isNullOrBlank() -> {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = category.name,
                                modifier = Modifier.size(72.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        category.imageRes != 0 -> {
                            Image(
                                painter = painterResource(id = category.imageRes),
                                contentDescription = category.name,
                                modifier = Modifier.size(72.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        else -> {
                            // fallback iniciales
                            Text(
                                text = category.name.take(1).uppercase(),
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            // 🔶 Scrim inferior + nombre superpuesto
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xAA000000))
                        ),
                        shape = CircleShape
                    )
            )

            Text(
                text = category.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}