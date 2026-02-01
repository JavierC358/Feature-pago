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

            // ✅ Foto existente (respaldo) para NO borrarla
            val currentDoc = firestore.collection("profile").document(uid).get().await()
            val existingPhotoUrl = currentDoc.getString("photoUrl").orEmpty()

            // ✅ Subir nueva si hay, si no conservar la que exista
            val finalPhotoUrl = try {
                imageUri?.let {
                    val imageRef = storage.reference.child("profile_pictures/$uid/profile.jpg")
                    imageRef.putFile(it).await()
                    imageRef.downloadUrl.await().toString()
                } ?: when {
                    profile.photoUrl.isNotBlank() -> profile.photoUrl
                    else -> existingPhotoUrl
                }
            } catch (_: Exception) {
                when {
                    profile.photoUrl.isNotBlank() -> profile.photoUrl
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