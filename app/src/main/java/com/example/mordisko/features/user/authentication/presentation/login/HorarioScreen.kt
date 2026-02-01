package com.example.mordisko.features.user.authentication.presentation.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

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
    val grayDisabled = Color(0xFFDADADA)
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
                text = "📍 Calle Santa Teresa c/c Calle 1ero de Mayo, San José de Guanipa.",
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
                onClick = {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid == null) {
                        onContinuar()
                        return@Button
                    }

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .set(
                            mapOf(
                                "termsAccepted" to true,
                                "termsAcceptedAt" to FieldValue.serverTimestamp()
                            ),
                            SetOptions.merge()
                        )
                        .addOnSuccessListener { onContinuar() }
                        .addOnFailureListener { onContinuar() }
                },
                enabled = aceptaTerminos,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp) // 🔥 mismo ancho que las tarjetas
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (aceptaTerminos) Color.White else grayDisabled,
                    contentColor = if (aceptaTerminos) orange else Color.Black,
                    disabledContainerColor = grayDisabled,
                    disabledContentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp) // igual estilo suave
            ) {
                Text(
                    text = "Ir al Menú",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}