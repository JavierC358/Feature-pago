package com.example.mordisko.features.user.authentication.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigation>(SplashNavigation.Loading)
    val navigationState: StateFlow<SplashNavigation> = _navigationState

    init {
        viewModelScope.launch {
            delay(3000)
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                try {
                    currentUser.reload().await() // 👈 Usa await() aquí
                    val uid = currentUser.uid
                    val db = FirebaseFirestore.getInstance()
                    val document = db.collection("users").document(uid).get().await()
                    val rol = document.getString("rol") ?: "cliente"
                    Log.d("NAV_TEST", "🎯 Rol detectado: $rol")

                    if (rol == "admin") {
                        _navigationState.value = SplashNavigation.AdminPanel
                    } else {
                        Log.d("NAV_TEST", "Asignando navegación a Horario desde SplashViewModel")
                        _navigationState.value = SplashNavigation.Horario // ✅ Aquí lo enviamos bien
                    }
                } catch (e: Exception) {
                    firebaseAuth.signOut()
                    _navigationState.value = SplashNavigation.Login
                }
            } else {
                _navigationState.value = SplashNavigation.Login
            }
        }
    }
}