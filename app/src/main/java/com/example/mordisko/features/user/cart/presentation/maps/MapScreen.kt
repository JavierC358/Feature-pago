package com.example.mordisko.features.user.cart.presentation.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mordisko.features.user.delivery.presentation.viewmodel.DeliveryViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    cartViewModel: CartViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    deliveryViewModel: DeliveryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var location by remember { mutableStateOf<Location?>(null) }
    val defaultLatLng = LatLng(8.88902, -64.2527) // fallback

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 17f)
    }

    var addressText by remember { mutableStateOf("Ubicación no determinada") }
    var mapReady by remember { mutableStateOf(false) }

    // ✅ Estado para cobertura/distancia/costo
    var distanciaKm by remember { mutableStateOf<Double?>(null) }
    var costoUsd by remember { mutableStateOf<Double?>(null) }
    var fueraDeZona by remember { mutableStateOf(false) }
    val settings by deliveryViewModel.settings.collectAsState()

    // ✅ Debounce de posición (emite centro y espera 600 ms)
    val markerFlow = remember { MutableSharedFlow<LatLng>(extraBufferCapacity = 1) }

    // ✅ Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    var yaAvisadoFueraZona by remember { mutableStateOf(false) }

    // 0) Cargar ajustes (incluye maxKm)
    LaunchedEffect(Unit) { deliveryViewModel.loadDeliverySettings() }

    // 1) Pedir permiso al entrar
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // 2) Obtener ubicación SOLO si ya hay permiso (chequeo explícito)
    LaunchedEffect(locationPermissionState.status) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine && !mapReady) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                val result = fusedClient.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                val latLng = result?.let {
                    location = it
                    LatLng(it.latitude, it.longitude)
                } ?: defaultLatLng

                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                mapReady = true
                markerFlow.tryEmit(latLng)
            } catch (_: Exception) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLatLng, 17f)
                mapReady = true
                markerFlow.tryEmit(defaultLatLng)
            }
        }
    }

    // 3) Cada vez que el mapa se "detiene", emitimos el centro (pero NO hacemos geocoding aquí)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (mapReady && !cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            markerFlow.tryEmit(center) // ← luego se procesa con debounce
        }
    }

    // 4) Procesar el centro del mapa con debounce (geocoding + distancia + costo)
    LaunchedEffect(Unit) {
        markerFlow
            .debounce(600) // ✅ espera a que el usuario deje de mover el mapa
            .collectLatest { center ->
                // Geocoding (seguro)
                addressText = try {
                    Geocoder(context).getFromLocation(center.latitude, center.longitude, 1)
                        ?.firstOrNull()?.getAddressLine(0) ?: "Dirección desconocida"
                } catch (_: Exception) {
                    "Dirección desconocida"
                }

                // Guardar en VM de carrito (como ya hacías)
                cartViewModel.setCoordinates(center.latitude, center.longitude)

                // Distancia / Cobertura
                val d = deliveryViewModel.distanciaDesdeTienda(center.latitude, center.longitude)
                distanciaKm = d
                val fuera = !deliveryViewModel.estaEnCobertura(center.latitude, center.longitude)
                fueraDeZona = fuera

                // Costo (solo si está en zona)
                costoUsd = if (!fuera) {
                    deliveryViewModel.calcularCostoDelivery(center.latitude, center.longitude)
                } else null

                // Snackbar (solo cuando pasa a fuera de zona por primera vez para evitar spam)
                if (fuera && !yaAvisadoFueraZona) {
                    yaAvisadoFueraZona = true
                    snackbarHostState.showSnackbar(
                        message = "Fuera de zona de cobertura (máx ${settings.maxKm} km)."
                    )
                }
                if (!fuera) {
                    yaAvisadoFueraZona = false // si vuelve a zona, resetea para avisar de nuevo si sale
                }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            TextButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Text("Volver")
            }

            // UI para cuando no hay permiso aún
            if (!locationPermissionState.status.isGranted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Necesitamos tu ubicación para seleccionar la dirección en el mapa.")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                            Text("Conceder permiso")
                        }
                    }
                }
                return@Column
            }

            if (mapReady) {
                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    )

                    // 📍 Indicador fijo en el centro (mueves el mapa)
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación seleccionada",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(42.dp)
                    )
                }

                Text(
                    text = "Mueve el mapa para ajustar la ubicación.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Cargando mapa...")
                }
            }

            // ✅ Banner con costo estimado (solo si está en zona)
            if (!fueraDeZona && distanciaKm != null && costoUsd != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Distancia: ${"%.2f".format(distanciaKm)} km")
                        Spacer(Modifier.weight(1f))
                        Text("Costo: $${"%.2f".format(costoUsd)} USD")
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Info básica (dirección y distancia + aviso si fuera de zona)
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Dirección: $addressText")
                Spacer(Modifier.height(6.dp))
                val dTxt = distanciaKm?.let { String.format("%.2f km", it) } ?: "—"
                Text(text = "Distancia a tienda: $dTxt")
                if (fueraDeZona) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Fuera de zona de cobertura (máx ${settings.maxKm} km).",
                        color = Color.Red
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val center = cameraPositionState.position.target
                    cartViewModel.setMainLocation(center)
                    cartViewModel.setSecondaryAddress(addressText)
                    cartViewModel.setCoordinates(center.latitude, center.longitude)
                    onConfirm()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = mapReady && !fueraDeZona
            ) {
                Text("Confirmar ubicación")
            }
        }
    }
}