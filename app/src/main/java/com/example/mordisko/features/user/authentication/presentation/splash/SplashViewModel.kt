package com.example.mordisko.features.user.authentication.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigation>(SplashNavigation.Loading)
    val navigationState: StateFlow<SplashNavigation> = _navigationState

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var alreadyNavigated = false

    init {
        authListener = FirebaseAuth.AuthStateListener { auth ->
            if (alreadyNavigated) return@AuthStateListener

            val user = auth.currentUser
            if (user == null) {
                alreadyNavigated = true
                _navigationState.value = SplashNavigation.Login
                return@AuthStateListener
            }

            // Si hay usuario, validamos rol en Firestore
            viewModelScope.launch {
                handleLoggedUser(user)
            }
        }

        firebaseAuth.addAuthStateListener(authListener!!)
    }

    private suspend fun handleLoggedUser(user: FirebaseUser) {
        if (alreadyNavigated) return

        try {
            // Asegura estado real del usuario (por si está "stale")
            user.reload().await()

            val uid = user.uid
            val db = FirebaseFirestore.getInstance()

            val document = db.collection("users").document(uid).get().await()
            val rol = document.getString("rol") ?: "cliente"

            Log.d("NAV_TEST", "🎯 Rol detectado: $rol")

            alreadyNavigated = true
            if (rol == "admin") {
                _navigationState.value = SplashNavigation.AdminPanel
            } else {
                Log.d("NAV_TEST", "Asignando navegación a Horario desde SplashViewModel")
                _navigationState.value = SplashNavigation.Horario
            }

        } catch (e: Exception) {
            Log.e("SplashViewModel", "Error validando sesión/rol: ${e.message}", e)
            firebaseAuth.signOut()

            alreadyNavigated = true
            _navigationState.value = SplashNavigation.Login
        }
    }

    override fun onCleared() {
        super.onCleared()
        authListener?.let { firebaseAuth.removeAuthStateListener(it) }
        authListener = null
    }
}