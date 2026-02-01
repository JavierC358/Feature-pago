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

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            val cleanEmail = email.trim()
            val cleanPass  = password.trim()
            firebaseAuth.signInWithEmailAndPassword(cleanEmail, cleanPass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al iniciar sesión", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserRole(): String? {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return null
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("rol") ?: doc.getString("role") ?: "cliente"
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al obtener rol de usuario", e)
            null
        }
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            val cleanEmail = email.trim()
            val cleanPass  = password.trim()

            firebaseAuth.createUserWithEmailAndPassword(cleanEmail, cleanPass).await()

            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo obtener el uid del usuario"))

            val initialProfile = mapOf(
                "correo" to cleanEmail,
                "nombre" to "",
                "telefono" to "",
                "nacionalidad" to "",
                "cedula" to "",
                "photoUrl" to ""
            )

            firestore.collection("profile").document(uid).set(initialProfile).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LoginRepository", "Error al registrar usuario", e)
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}