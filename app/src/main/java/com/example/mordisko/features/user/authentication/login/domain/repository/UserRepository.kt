package com.example.mordisko.features.user.authentication.login.domain.repository

interface UserRepository {
    suspend fun getUserRole(uid: String): String?
}