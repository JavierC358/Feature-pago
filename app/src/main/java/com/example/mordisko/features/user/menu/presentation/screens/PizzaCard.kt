package com.example.mordisko.features.user.menu.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.example.mordisko.R

@Composable
fun PizzaCard(
    pizza: PizzaItem,
    onClick: () -> Unit,
    nameColor: Color = Color(0xFFE05B13),
    paddingHorizontal: Dp = 0.dp
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal)
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🧀 Nombre de la pizza arriba
            Text(
                text = pizza.name,
                color = nameColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 🍕 Imagen debajo del nombre
            AsyncImage(
                model = pizza.imageUrl,
                contentDescription = pizza.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}