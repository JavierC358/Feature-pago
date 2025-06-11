package com.example.mordisko.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.menu.domain.model.PizzaCategory
import com.example.mordisko.features.menu.domain.model.pizzaCategories

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategorySelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val orange = Color(0xFFE05B13)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(orange)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Nuestro Menu!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp)) // reducido

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp), // reducido
            horizontalArrangement = Arrangement.spacedBy(8.dp), // reducido
            modifier = Modifier
                .padding(4.dp) // reducido
                .weight(1f)
        ) {
            items(pizzaCategories) { category ->
                PizzaCategoryCard(
                    category = category,
                    onClick = { onCategorySelected(category.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogout() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = orange
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PizzaCategoryCard(category: PizzaCategory, onClick: () -> Unit) {
    val orange = Color(0xFFE05B13)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = category.imageRes),
                    contentDescription = category.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.8f)
                )
            }

            // Nombre de la categoría en la parte inferior
            Text(
                text = category.name,
                color = orange,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 6.dp)
            )
        }
    }
}