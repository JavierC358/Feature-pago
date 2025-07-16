package com.example.mordisko.features.user.authentication.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElegirRolScreen(
    onClienteSelected: () -> Unit,
    onAdminSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cómo deseas ingresar?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onClienteSelected) {
            Text("Entrar como Cliente")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAdminSelected) {
            Text("Entrar como Administrador")
        }
    }
}
