package com.example.mordisko.features.user.profile.domain.repository

import android.net.Uri
import com.example.mordisko.features.user.profile.domain.model.UserProfile

interface UserRepository {
    suspend fun saveUserProfile(profile: UserProfile, imageUri: Uri?): Result<UserProfile>
    suspend fun getUserProfile(): Result<UserProfile>
}