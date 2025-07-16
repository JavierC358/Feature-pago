package com.example.mordisko.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


data class OrdenResumen(
    val orderNumber: String,
    val timestamp: Date,
    val totalUsd: Double,
    val totalBs: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenDeOrdenesScreen(
    fechaDesde: Date,
    fechaHasta: Date,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var ordenes by remember { mutableStateOf<List<OrdenResumen>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(fechaDesde, fechaHasta) {
        cargando = true
        val snapshot = db.collection("orders")
            .whereGreaterThanOrEqualTo("timestamp", Timestamp(fechaDesde))
            .whereLessThanOrEqualTo("timestamp", Timestamp(fechaHasta))
            .get()
            .await()

        val lista = snapshot.documents.mapNotNull { doc ->
            val paymentStatus = doc.getString("paymentStatus") ?: return@mapNotNull null
            if (paymentStatus != "verificado") return@mapNotNull null

            val orderNumber = doc.getString("orderNumber") ?: return@mapNotNull null
            val timestamp = doc.getTimestamp("timestamp")?.toDate() ?: return@mapNotNull null
            val totalUsd = doc.getDouble("totalUsd") ?: 0.0
            val totalBs = doc.getDouble("totalBs") ?: 0.0

            OrdenResumen(orderNumber, timestamp, totalUsd, totalBs)
        }

        ordenes = lista
        cargando = false
    }

    val totalUsd = ordenes.sumOf { it.totalUsd }
    val totalBs = ordenes.sumOf { it.totalBs }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Órdenes del ${dateFormat.format(fechaDesde)} al ${dateFormat.format(fechaHasta)}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ordenes) { orden ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Orden: ${orden.orderNumber}", style = MaterialTheme.typography.titleMedium)
                                Text("Fecha: ${dateFormat.format(orden.timestamp)}")
                                Text("Total USD: $${String.format("%.2f", orden.totalUsd)}")
                                Text("Total Bs: ${String.format("%,.2f", orden.totalBs)} Bs")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Tarjeta de Totales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Totales", style = MaterialTheme.typography.titleLarge)
                        Text("Total USD: $${String.format("%.2f", totalUsd)}")
                        Text("Total Bs: ${String.format("%,.2f", totalBs)} Bs")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Botón Exportar PDF (una sola vez, al final)
                val context = LocalContext.current

                Button(
                    onClick = {
                        compartirPdf(context, ordenes, totalUsd, totalBs, fechaDesde, fechaHasta)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Compartir PDF")
                }
            }
        }
    }
}

fun generarResumenPdf(
    context: Context,
    ordenes: List<OrdenResumen>,
    totalUsd: Double,
    totalBs: Double,
    fechaDesde: Date,
    fechaHasta: Date
) {
    val pdfDocument = PdfDocument()
    val paint = android.graphics.Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var y = 50

    // ✅ TÍTULO PRINCIPAL
    paint.textSize = 20f
    paint.isFakeBoldText = true
    paint.color = android.graphics.Color.BLACK
    canvas.drawText("Resumen de órdenes verificadas", 40f, y.toFloat(), paint)
    y += 28

    // ✅ FECHA DE PERÍODO
    paint.textSize = 14f
    paint.isFakeBoldText = false
    canvas.drawText("Período: ${dateFormat.format(fechaDesde)} al ${dateFormat.format(fechaHasta)}", 40f, y.toFloat(), paint)
    y += 30

    ordenes.forEach { orden ->
        if (y > 740) return@forEach

        val top = y
        val bottom = y + 70

        // ✅ Fondo de tarjeta
        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.rgb(245, 245, 245)
        canvas.drawRect(30f, top.toFloat(), 565f, bottom.toFloat(), paint)

        // ✅ Borde de tarjeta
        paint.style = android.graphics.Paint.Style.STROKE
        paint.color = android.graphics.Color.DKGRAY
        paint.strokeWidth = 1.5f
        canvas.drawRect(30f, top.toFloat(), 565f, bottom.toFloat(), paint)

        // ✅ Contenido de tarjeta
        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Orden: ${orden.orderNumber}", 40f, (y + 22).toFloat(), paint)
        val fechaOrden = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(orden.timestamp)
        canvas.drawText("Fecha: $fechaOrden", 300f, (y + 22).toFloat(), paint)

        paint.isFakeBoldText = false
        canvas.drawText("USD: $${String.format("%.2f", orden.totalUsd)}", 40f, (y + 45).toFloat(), paint)
        canvas.drawText("Bs: ${String.format("%,.2f", orden.totalBs)}", 300f, (y + 45).toFloat(), paint)

        y += 85
    }

    // ✅ Totales
    paint.textSize = 16f
    paint.isFakeBoldText = true
    y += 10
    canvas.drawText("TOTAL USD: $${String.format("%.2f", totalUsd)}", 40f, y.toFloat(), paint)
    y += 22
    canvas.drawText("TOTAL Bs: ${String.format("%,.2f", totalBs)}", 40f, y.toFloat(), paint)

    pdfDocument.finishPage(page)

    // ✅ Guardar
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(dir, "resumen_ordenes.pdf")

    try {
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        Toast.makeText(context, "PDF generado en ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}

fun compartirPdf(
    context: Context,
    ordenes: List<OrdenResumen>,
    totalUsd: Double,
    totalBs: Double,
    fechaDesde: Date,
    fechaHasta: Date
) {
    // 1️⃣ Generar PDF antes de compartir
    generarResumenPdf(context, ordenes, totalUsd, totalBs, fechaDesde, fechaHasta)

    // 2️⃣ Compartir el PDF
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "resumen_ordenes.pdf")
    if (!file.exists()) {
        Toast.makeText(context, "No se ha encontrado el PDF", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Compartir PDF con..."))
}