package com.example.mordisko.features.user.authentication.presentation.splash

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
                    currentUser.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful && firebaseAuth.currentUser != null) {
                            val uid = currentUser.uid
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    val rol = document.getString("rol") ?: "cliente"
                                    if (rol == "admin") {
                                        _navigationState.value = SplashNavigation.AdminPanel
                                    } else {
                                        _navigationState.value = SplashNavigation.Home
                                    }
                                }
                                .addOnFailureListener {
                                    // Si falla la consulta de rol, por seguridad va a login
                                    firebaseAuth.signOut()
                                    _navigationState.value = SplashNavigation.Login
                                }
                        } else {
                            firebaseAuth.signOut()
                            _navigationState.value = SplashNavigation.Login
                        }
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