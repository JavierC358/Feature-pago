package com.example.mordisko.features.user.menu.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mordisko.features.user.menu.domain.model.PizzaItem

@Composable
fun PizzaCard(
    pizza: PizzaItem,
    onClick: () -> Unit,
    nameColor: Color = Color(0xFFE05B13),
    paddingHorizontal: Dp = 0.dp
) {
    // ✅ Loader por tarjeta (aparece mientras la imagen carga)
    var isImageLoading by remember(pizza.imageUrl) { mutableStateOf(true) }

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
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 🍕 Imagen debajo del nombre + loader centrado mientras carga
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pizza.imageUrl,
                    contentDescription = pizza.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    onLoading = { isImageLoading = true },
                    onSuccess = { isImageLoading = false },
                    onError = { isImageLoading = false }
                )

                if (isImageLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}