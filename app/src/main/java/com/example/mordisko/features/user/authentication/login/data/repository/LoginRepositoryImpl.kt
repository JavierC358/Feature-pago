package com.example.mordisko.features.user.authentication.login.data.repository

import android.util.Log
import com.example.mordisko.features.user.authentication.login.domain.repository.LoginRepository
import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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

    override suspend fun getUserRole(): String? {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return null
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("rol") ?: "cliente"
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al obtener rol de usuario", e)
            null
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