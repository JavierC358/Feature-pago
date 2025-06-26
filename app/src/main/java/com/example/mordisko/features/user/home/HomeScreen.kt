package com.example.mordisko.features.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaCategory
import com.example.mordisko.features.user.menu.domain.model.pizzaCategories
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategorySelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val orange = Color(0xFFE05B13)

    val centerCategory = pizzaCategories.firstOrNull { it.name.lowercase() == "pizzas" } ?: pizzaCategories.first()
    val otherCategories = pizzaCategories.filterNot { it == centerCategory }

    val radius = 130.dp
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(orange)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Nuestro Menu!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Centro: Pizzas
            CircularCategoryCard(category = centerCategory, onClick = { onCategorySelected(centerCategory.name) })

            // Alrededor: otras categorías
            otherCategories.forEachIndexed { index, category ->
                val angleDeg = (360f / otherCategories.size) * index - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val radiusPx = with(density) { radius.toPx() }
                val offsetX = radiusPx * cos(angleRad)
                val offsetY = radiusPx * sin(angleRad)

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(offsetX.toInt(), offsetY.toInt())
                        }
                ) {
                    CircularCategoryCard(category = category, onClick = { onCategorySelected(category.name) })
                }
            }
        }

        Button(
            onClick = { onLogout() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = orange
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Cerrar sesión",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CircularCategoryCard(category: PizzaCategory, onClick: () -> Unit) {
    val orange = Color(0xFFE05B13)

    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(75.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                fontSize = 11.sp,
                color = orange,
                fontWeight = FontWeight.Bold
            )
        }
    }
}