package com.example.mordisko.features.user.authentication.presentation.login

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mordisko.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticaPrivacidadScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var textContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        textContent = loadTextFromRawResource(context, R.raw.politica_privacidad)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState) // ⭐️ Aquí activamos el scroll
        ) {
            Text(
                text = "Política de Privacidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = textContent,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

fun loadTextFromRawResource(context: Context, resourceId: Int): String {
    return context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
}