package com.example.mordisko.features.authentication.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
            delay(3000) // tiempo de espera en splash
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                try {
                    currentUser.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful && firebaseAuth.currentUser != null) {
                            _navigationState.value = SplashNavigation.Home
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