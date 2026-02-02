package com.example.mordisko.features.user.profile.data.repository

import android.net.Uri
import com.example.mordisko.features.user.profile.domain.model.UserProfile
import com.example.mordisko.features.user.profile.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : UserRepository {

    override suspend fun saveUserProfile(profile: UserProfile, imageUri: Uri?): Result<UserProfile> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Foto existente como respaldo (por si NO seleccionan una nueva)
            val currentDoc = firestore.collection("profile").document(uid).get().await()
            val existingPhotoUrl = currentDoc.getString("photoUrl").orEmpty()

            val finalPhotoUrl = if (imageUri != null) {
                // ✅ Si hay imagen nueva, DEBE subirse a Storage sí o sí
                val imageRef = storage.reference.child("profile_pictures/$uid/profile.jpg")
                imageRef.putFile(imageUri).await()
                imageRef.downloadUrl.await().toString()
            } else {
                // ✅ Si NO hay imagen nueva, conservamos la anterior (o la que ya traiga el profile si es válida)
                when {
                    profile.photoUrl.isNotBlank() && !profile.photoUrl.startsWith("content://") -> profile.photoUrl
                    else -> existingPhotoUrl
                }
            }

            val profileToSave = profile.copy(photoUrl = finalPhotoUrl)

            firestore.collection("profile")
                .document(uid)
                .set(profileToSave, SetOptions.merge())
                .await()

            Result.success(profileToSave)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))
            val doc = firestore.collection("profile").document(uid).get().await()
            val profile = doc.toObject(UserProfile::class.java) ?: UserProfile()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}