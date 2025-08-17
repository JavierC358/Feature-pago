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

    override suspend fun saveUserProfile(profile: UserProfile, imageUri: Uri?): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Subir imagen si se seleccionó, sino conservar la anterior
            val finalPhotoUrl = try {
                imageUri?.let {
                    val imageRef = storage.reference.child("profile_pictures/$uid/profile.jpg")
                    imageRef.putFile(it).await()
                    imageRef.downloadUrl.await().toString()
                } ?: profile.photoUrl
            } catch (_: Exception) {
                profile.photoUrl // si la subida falla, no afecta el resto de datos
            }

            val profileToSave = profile.copy(photoUrl = finalPhotoUrl)

            // Guardar con merge para no borrar otros campos existentes
            firestore.collection("profile")
                .document(uid)
                .set(profileToSave, SetOptions.merge())
                .await()

            Result.success(Unit)
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