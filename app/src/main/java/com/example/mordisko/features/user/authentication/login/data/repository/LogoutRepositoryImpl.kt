package com.example.mordisko.features.user.authentication.login.data.repository

import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LogoutRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LogoutRepository {

    override fun logout() {
        firebaseAuth.signOut()
    }
}