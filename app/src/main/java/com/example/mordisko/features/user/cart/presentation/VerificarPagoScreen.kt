package com.example.mordisko.features.user.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.admin.presentation.viewmodel.PaymentVerificationViewModel
import kotlinx.coroutines.launch

// ✅ NUEVOS IMPORTS (teclado)
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

private fun filterDigitsOnly(input: String, maxLen: Int): String {
    return input.filter { it.isDigit() }.take(maxLen)
}

private fun filterMoneyDecimal(input: String, maxDecimals: Int = 2): String {
    if (input.isBlank()) return ""

    val cleaned = input.filter { it.isDigit() || it == '.' || it == ',' }
    val normalized = cleaned.replace(',', '.')

    val parts = normalized.split('.')
    val integerPart = parts.getOrNull(0)?.filter { it.isDigit() } ?: ""

    val decimalPartRaw = parts.drop(1).joinToString("") // si metieron varios puntos
    val decimalPart = decimalPartRaw.filter { it.isDigit() }.take(maxDecimals)

    return if (normalized.contains('.')) {
        // Permite "123." mientras escribe
        if (decimalPartRaw.isEmpty()) "$integerPart."
        else "$integerPart.$decimalPart"
    } else {
        integerPart
    }
}

@Composable
fun VerificarPagoScreen(
    orderNumber: String,
    onCerrar: () -> Unit,
    onVerificacionEnviada: (String) -> Unit,
    viewModel: PaymentVerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    val orange = Color(0xFFE05B13)

    var monto by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var deseaFactura by remember { mutableStateOf(false) }
    var razonSocial by remember { mutableStateOf("") }
    var rif by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val montoValido = monto.isNotBlank() && monto != "." && !monto.endsWith(".")
    val puedeVerificar = montoValido &&
            referencia.length == 4 &&
            telefono.length == 11 &&
            (!deseaFactura || (razonSocial.isNotBlank() && rif.isNotBlank() && direccion.isNotBlank()))

    // Mostrar Snackbar de éxito
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("¡Verificación enviada con éxito!")
            viewModel.resetState()
            onVerificacionEnviada(orderNumber)
        }
    }

    // Mostrar Snackbar de error
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Error: $it")
                viewModel.resetState()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Suministra la información exacta de tu pago para verificarlo.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = orange
                    )

                    // ✅ MONTO: solo números + decimal
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = filterMoneyDecimal(it, maxDecimals = 2) },
                        label = { Text("Monto pagado (Bs.)") },
                        placeholder = { Text("Ej: 109.50") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ✅ REFERENCIA: solo números, máx 4
                    OutlinedTextField(
                        value = referencia,
                        onValueChange = { referencia = filterDigitsOnly(it, maxLen = 4) },
                        label = { Text("Últimos 4 dígitos de la referencia") },
                        placeholder = { Text("Ej: 4582") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ✅ TELÉFONO: solo números, máx 11 + cerrar teclado en Done
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = filterDigitsOnly(it, maxLen = 11) },
                        label = { Text("Teléfono desde donde pagaste") },
                        placeholder = { Text("Ej: 04141234567") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = deseaFactura,
                            onCheckedChange = { deseaFactura = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = orange,
                                uncheckedColor = orange
                            )
                        )
                        Text("Solo si deseas factura")
                    }

                    if (deseaFactura) {
                        OutlinedTextField(
                            value = razonSocial,
                            onValueChange = { razonSocial = it },
                            label = { Text("Razón social") },
                            placeholder = { Text("Ej: Inversiones Mordisko C.A.") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = rif,
                            onValueChange = { rif = it },
                            label = { Text("Rif") },
                            placeholder = { Text("Ej: J-12345678-9") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { direccion = it },
                            label = { Text("Dirección") },
                            placeholder = { Text("Ej: Av. Principal, Local 2...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCerrar,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar")
                    }

                    Button(
                        onClick = {
                            android.util.Log.d(
                                "VerifyUI",
                                "click Verificar order=$orderNumber monto=$monto ref=$referencia tel=$telefono"
                            )
                            viewModel.submitPaymentVerification(
                                orderNumber,
                                monto,
                                referencia,
                                telefono,
                                razonSocial.takeIf { deseaFactura },
                                rif.takeIf { deseaFactura },
                                direccion.takeIf { deseaFactura }
                            )
                        },
                        enabled = puedeVerificar && !state.isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = orange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (state.isLoading) "Enviando..." else "Verificar")
                    }
                }
            }
        }
    }
}