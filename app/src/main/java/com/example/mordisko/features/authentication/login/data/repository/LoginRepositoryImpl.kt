package com.example.mordisko.features.authentication.login.data.repository

import android.util.Log
import com.example.mordisko.features.authentication.login.domain.repository.LoginRepository
import com.example.mordisko.features.authentication.login.domain.repository.LogoutRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository, LogoutRepository {

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al iniciar sesión", e)
            false
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al registrar usuario", e)
            false
        }
    }
}