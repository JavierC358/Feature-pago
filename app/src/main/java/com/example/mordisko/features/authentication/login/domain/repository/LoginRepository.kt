package com.example.mordisko.features.authentication.login.domain.repository

interface LoginRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String): Boolean
    suspend fun registerWithEmailAndPassword(email: String, password: String): Boolean
    fun logout()
}