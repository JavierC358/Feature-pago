package com.example.mordisko.features.admin.categories.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mordisko.features.admin.categories.domain.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    onBack: () -> Unit,
    onPickImageForCategory: (categoryId: String) -> Unit = {},
    // La ruta registra aquí un setter para enviar (id, url) cuando termine la subida
    registerUpdateImageCallback: ((String, String) -> Unit) -> Unit = {},
    viewModel: AdminCategoriesViewModel = hiltViewModel()
) {
    // ✅ Registrar el callback que actualiza la imagen en el VM
    LaunchedEffect(Unit) {
        registerUpdateImageCallback { id, url ->
            viewModel.updateImageUrl(id, url)
        }
    }

    val items by viewModel.items.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showNewDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showNewDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Nueva categoría")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            when {
                items.isEmpty() && !loading -> EmptyState(onCreateDemo = {
                    val name = "Categoría demo"
                    viewModel.createCategory(name)
                })

                else -> CategoriesList(
                    items = items,
                    onMoveUp = { idx -> viewModel.moveUp(idx) },
                    onMoveDown = { idx -> viewModel.moveDown(idx) },
                    onToggleVisible = { id, visible -> viewModel.toggleVisible(id, visible) },
                    onEditName = { id, name -> viewModel.updateName(id, name) },
                    onChangeImage = { id -> onPickImageForCategory(id) }
                )
            }
        }
    }

    if (showNewDialog) {
        AlertDialog(
            onDismissRequest = { showNewDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val name = newName.text.trim()
                    if (name.isNotEmpty()) {
                        viewModel.createCategory(name)
                        newName = TextFieldValue("")
                        showNewDialog = false
                    }
                }) { Text("Crear") }
            },
            dismissButton = { TextButton(onClick = { showNewDialog = false }) { Text("Cancelar") } },
            title = { Text("Nueva categoría") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            }
        )
    }
}

@Composable
private fun EmptyState(onCreateDemo: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sin categorías aún", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onCreateDemo) { Text("Crear categoría demo") }
        }
    }
}

@Composable
private fun CategoriesList(
    items: List<Category>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onToggleVisible: (String, Boolean) -> Unit,
    onEditName: (String, String) -> Unit,
    onChangeImage: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(items, key = { _, it -> it.id }) { index, cat ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Fila 1: imagen + textos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = cat.imageUrl,
                            contentDescription = cat.name,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Posición: ${cat.position} • Visible: ${cat.visible}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Fila 2: acciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            IconButton(onClick = { onMoveUp(index) }, enabled = index > 0) {
                                Icon(Icons.Filled.ArrowUpward, contentDescription = "Subir")
                            }
                            IconButton(onClick = { onMoveDown(index) }, enabled = index < items.lastIndex) {
                                Icon(Icons.Filled.ArrowDownward, contentDescription = "Bajar")
                            }
                        }
                        Row {
                            IconButton(onClick = { onChangeImage(cat.id) }) {
                                Icon(Icons.Filled.Image, contentDescription = "Cambiar imagen")
                            }
                            IconButton(onClick = { onToggleVisible(cat.id, !cat.visible) }) {
                                Icon(
                                    if (cat.visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Visibilidad"
                                )
                            }

                            var showEdit by remember { mutableStateOf(false) }
                            IconButton(onClick = { showEdit = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar nombre")
                            }
                            if (showEdit) EditNameDialog(
                                initial = cat.name,
                                onDismiss = { showEdit = false },
                                onConfirm = { newName ->
                                    onEditName(cat.id, newName)
                                    showEdit = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditNameDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(initial)) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(text.text.trim()) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar nombre") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nombre") },
                singleLine = true
            )
        }
    )
}