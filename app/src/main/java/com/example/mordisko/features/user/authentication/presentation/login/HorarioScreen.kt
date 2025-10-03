package com.example.mordisko.features.user.authentication.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mordisko.R
import com.example.mordisko.core.navigation.Routes

@Composable
fun HorarioScreen(
    onContinuar: () -> Unit,
    onTerminos: () -> Unit,
    onPoliticas: () -> Unit,
    onAyuda: () -> Unit,
    onEditarPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    val orange = Color(0xFFE05B13)
    val lightOrange = Color(0xFFFFA726)
    var expanded by remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(lightOrange, orange)
                )
            )
            .padding(24.dp)
    ) {
        // Icono menú hamburguesa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Términos y Condiciones") },
                    onClick = {
                        expanded = false
                        onTerminos()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Article, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Políticas de Privacidad") },
                    onClick = {
                        expanded = false
                        onPoliticas()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PrivacyTip, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ayuda") },
                    onClick = {
                        expanded = false
                        onAyuda()   // ← aquí navegas a la ayuda
                    },
                    leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Editar perfil") },
                    onClick = {
                        expanded = false
                        onEditarPerfil()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Cerrar Sesión") },
                    onClick = {
                        expanded = false
                        onLogout()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                    }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo4a),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    //.clip(CircleShape)
                    .padding(8.dp)
            )

            Text(
                text = "Horario de Atención",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Martes a Viernes:" to "11:00 AM - 09:00 PM",
                    "Sábado:" to "10:00 AM - 09:00 PM",
                    "Domingo:" to "09:00 AM - 06:00 PM"
                ).forEach { (dia, hora) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = dia, fontWeight = FontWeight.Bold, color = orange)
                            Text(text = hora, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "📍 Calle Santa Teresa c/c Calle 1ero de Mayo Numero 100, San José de Guanipa.",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Checkbox de aceptación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        append("Acepto los ")
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Color.White)) {
                            append("términos y condiciones")
                        }
                        append(" de uso de Sazón.")
                    },
                    modifier = Modifier.clickable { onTerminos() },
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Ir al menú
            Button(
                onClick = onContinuar,
                enabled = aceptaTerminos,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = orange,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Ir al Menú", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}