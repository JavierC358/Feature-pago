package com.example.mordisko.features.admin.presentation.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mordisko.R
import com.example.mordisko.features.admin.presentation.viewmodel.AdminMenuViewModel
import com.example.mordisko.features.user.menu.domain.model.PizzaItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarImagenesScreen(
    viewModel: AdminMenuViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()

    LaunchedEffect(products) {
        Log.d("ActualizarImagenes", "🟢 Productos recibidos: ${products.size}")
    }

    val context = LocalContext.current
    val selectedProduct = remember { mutableStateOf<PizzaItem?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val imageGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedProduct.value?.let { product ->
                uploadImageAndUpdateProduct(
                    product = product,
                    imageUri = uri,
                    context = context,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    onRefresh = { viewModel.getAllProductsFromFirestore() }
                )
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                selectedProduct.value?.let { product ->
                    uploadImageAndUpdateProduct(
                        product = product,
                        imageUri = uri,
                        context = context,
                        snackbarHostState = snackbarHostState,
                        coroutineScope = coroutineScope,
                        onRefresh = { viewModel.getAllProductsFromFirestore() }
                    )
                }
            }
        }
    }

    val cameraPermission = android.Manifest.permission.CAMERA
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageFile(context)
            photoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllProductsFromFirestore()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualizar Imágenes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {

            if (products.isEmpty()) {
                item {
                    Text(
                        text = "🟡 No hay productos para mostrar",
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                Log.d("ActualizarImagenes", "✅ Productos listos para mostrar")
            }

            items(products) { product ->
                ImagenProductoCard(
                    product = product,
                    onCambiarImagen = {
                        selectedProduct.value = product
                        showImageSourceDialog = true
                    }
                )
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar fuente de imagen") },
            text = { Text("¿Desde dónde deseas seleccionar la imagen?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    if (ContextCompat.checkSelfPermission(context, cameraPermission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        val uri = createImageFile(context)
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        requestCameraPermissionLauncher.launch(cameraPermission)
                    }
                }) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        imageGalleryLauncher.launch("image/*")
                    }) {
                        Text("Galería")
                    }
                    TextButton(onClick = {
                        showImageSourceDialog = false
                    }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}

@Composable
fun ImagenProductoCard(
    product: PizzaItem,
    onCambiarImagen: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imagePainter = rememberAsyncImagePainter(
                model = product.imageUrl,
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_placeholder)
            )

            Image(
                painter = imagePainter,
                contentDescription = product.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = onCambiarImagen) {
                    Text("Cambiar imagen")
                }
            }
        }
    }
}

fun uploadImageAndUpdateProduct(
    product: PizzaItem,
    imageUri: Uri,
    context: Context,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRefresh: () -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("product_images/${product.name.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                FirebaseFirestore.getInstance()
                    .collection("products")
                    .document(product.id)
                    .update("imageUrl", downloadUrl.toString())
                    .addOnSuccessListener {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Imagen actualizada con éxito")
                        }
                        onRefresh()
                    }
                    .addOnFailureListener { e ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("❌ Error al actualizar Firestore: ${e.message}")
                        }
                    }
            }
        }
        .addOnFailureListener { e ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar("❌ Error al subir imagen: ${e.message}")
            }
        }
}

fun createImageFile(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    return FileProvider.getUriForFile(
        context,
        "com.example.mordisko.fileprovider",
        image
    )
}




