package com.example.mordisko.features.admin.presentation.screens

import android.content.ClipData
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
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.mordisko.BuildConfig
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
): File? {
    val pdfDocument = PdfDocument()
    val paint = android.graphics.Paint()
    val pageWidth = 595
    val pageHeight = 842
    var pageNumber = 1

    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    var y = 50

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun nuevaPagina() {
        pdfDocument.finishPage(page)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas
        y = 50
    }

    // Título
    paint.textSize = 20f
    paint.isFakeBoldText = true
    paint.color = android.graphics.Color.BLACK
    canvas.drawText("Resumen de órdenes verificadas", 40f, y.toFloat(), paint)
    y += 28
    paint.textSize = 14f
    paint.isFakeBoldText = false
    canvas.drawText("Período: ${dateFormat.format(fechaDesde)} al ${dateFormat.format(fechaHasta)}",40f,y.toFloat(),paint)
    y += 30

    ordenes.forEach { orden ->
        if (y > 740) nuevaPagina()

        val top = y
        val bottom = y + 70

        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.rgb(245, 245, 245)
        canvas.drawRect(30f, top.toFloat(), 565f, bottom.toFloat(), paint)

        paint.style = android.graphics.Paint.Style.STROKE
        paint.color = android.graphics.Color.DKGRAY
        paint.strokeWidth = 1.5f
        canvas.drawRect(30f, top.toFloat(), 565f, bottom.toFloat(), paint)

        paint.style = android.graphics.Paint.Style.FILL
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Orden: ${orden.orderNumber}",40f,(y+22).toFloat(),paint)
        val fechaOrden = dateFormat.format(orden.timestamp)
        canvas.drawText("Fecha: $fechaOrden",300f,(y+22).toFloat(),paint)

        paint.isFakeBoldText = false
        canvas.drawText("USD: $${String.format("%.2f", orden.totalUsd)}",40f,(y+45).toFloat(),paint)
        canvas.drawText("Bs: ${String.format("%,.2f", orden.totalBs)}",300f,(y+45).toFloat(),paint)

        y += 85
    }

    // Totales (si hay poco espacio, crear nueva página)
    if (y > 740) nuevaPagina()
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("TOTAL USD: $${String.format("%.2f", totalUsd)}",40f,y.toFloat(),paint)
    y += 22
    canvas.drawText("TOTAL Bs: ${String.format("%,.2f", totalBs)}",40f,y.toFloat(),paint)

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "resumen_ordenes.pdf")
    return try {
        FileOutputStream(file).use { pdfDocument.writeTo(it) }
        file
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        null
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
    // 1) Generar y obtener el archivo
    val file = generarResumenPdf(context, ordenes, totalUsd, totalBs, fechaDesde, fechaHasta)
    if (file == null || !file.exists() || file.length() == 0L) {
        Toast.makeText(context, "No se pudo preparar el PDF para compartir.", Toast.LENGTH_SHORT).show()
        return
    }

    // 2) URI de FileProvider (authority = ${applicationId}.provider)
    val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"
    val uri: Uri = try {
        FileProvider.getUriForFile(context, authority, file)   // ← usa la misma autoridad
    } catch (e: IllegalArgumentException) {
        Toast.makeText(context, "No se pudo obtener el URI del archivo.", Toast.LENGTH_SHORT).show()
        return
    }

    // 3) Intent para WhatsApp Business / WhatsApp
    val waBiz = "com.whatsapp.w4b"
    val wa = "com.whatsapp"

    // Intent principal (ACTION_SEND)
    val shareSingle = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        clipData = ClipData.newRawUri("PDF", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_SUBJECT, "Resumen de órdenes (PDF)")
        putExtra(Intent.EXTRA_TEXT, "")
    }

    // Intent fallback (ACTION_SEND_MULTIPLE) — algunos WA Business lo prefieren
    val uris = arrayListOf(uri)
    val shareMultiple = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "application/pdf"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        clipData = ClipData.newRawUri("PDFs", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_SUBJECT, "Resumen de órdenes (PDF)")
        putExtra(Intent.EXTRA_TEXT, "")
    }

    // 4) Preferir WhatsApp Business si está instalado; si no, WhatsApp normal
    val pm = context.packageManager
    val targetPkg = when {
        try { pm.getPackageInfo(waBiz, 0); true } catch (_: Exception) { false } -> waBiz
        try { pm.getPackageInfo(wa, 0); true } catch (_: Exception) { false } -> wa
        else -> null
    }

    try {
        if (targetPkg != null) {
            // Conceder permiso explícito
            context.grantUriPermission(targetPkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Probar con ACTION_SEND primero
            shareSingle.setPackage(targetPkg)
            context.startActivity(shareSingle)
        } else {
            // Sin WhatsApp: abrir chooser con ACTION_SEND
            val resList = pm.queryIntentActivities(shareSingle, PackageManager.MATCH_DEFAULT_ONLY)
            for (info in resList) {
                context.grantUriPermission(info.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareSingle, "Compartir PDF con...").apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    } catch (_: Exception) {
        // Si falla con ACTION_SEND, intentamos ACTION_SEND_MULTIPLE directo al mismo paquete (si hay)
        try {
            if (targetPkg != null) {
                context.grantUriPermission(targetPkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareMultiple.setPackage(targetPkg)
                context.startActivity(shareMultiple)
            } else {
                val resList = pm.queryIntentActivities(shareMultiple, PackageManager.MATCH_DEFAULT_ONLY)
                for (info in resList) {
                    context.grantUriPermission(info.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(shareMultiple, "Compartir PDF con...").apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo compartir el PDF.", Toast.LENGTH_SHORT).show()
        }
    }
}