package com.example.mordisko.features.user.cart.presentation.maps

import android.Manifest
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mordisko.features.user.cart.presentation.CartViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    cartViewModel: CartViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var location by remember { mutableStateOf<Location?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    var addressText by remember { mutableStateOf("Ubicación no determinada") }

    var mapReady by remember { mutableStateOf(false) } // ✅ Evita mostrar mapa sin ubicación

    val defaultLatLng = LatLng(10.4806, -66.9036) // Caracas fallback

    // Solicitar permisos
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Obtener ubicación actual y mover cámara
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted && !mapReady) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)

            try {
                val permissionGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (permissionGranted) {
                    val result = fusedClient.lastLocation.await()
                    val latLng = result?.let {
                        location = it
                        LatLng(it.latitude, it.longitude)
                    } ?: defaultLatLng

                    markerState.position = latLng
                    cameraPositionState.position = CameraPositionState(
                        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(latLng, 17f)
                    ).position

                    mapReady = true // ✅ Solo se activa cuando hay LatLng
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualizar dirección al mover el marcador
    LaunchedEffect(markerState.position) {
        val newLatLng = markerState.position
        val addresses = Geocoder(context).getFromLocation(newLatLng.latitude, newLatLng.longitude, 1)
        addressText = addresses?.firstOrNull()?.getAddressLine(0) ?: "Dirección desconocida"
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TextButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Text("Volver")
        }

        if (mapReady) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = markerState,
                    title = "Ubicación seleccionada",
                    draggable = true
                )
            }

            Text(
                text = "Puedes mover el marcador para seleccionar otra ubicación.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Cargando mapa...")
            }
        }

        Text(
            text = "Dirección: $addressText",
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                cartViewModel.setMainLocation(markerState.position)
                cartViewModel.setSecondaryAddress(addressText)
                onConfirm()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = mapReady
        ) {
            Text("Confirmar ubicación")
        }
    }
}