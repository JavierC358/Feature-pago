package com.example.mordisko.features.user.authentication.login.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.mordisko.features.user.authentication.login.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun getUserRole(uid: String): String? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("role")
        } catch (e: Exception) {
            null
        }
    }
}