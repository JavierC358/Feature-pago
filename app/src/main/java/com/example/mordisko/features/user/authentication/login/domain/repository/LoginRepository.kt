
package com.example.mordisko.features.user.authentication.login.domain.repository
interface LoginRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun getUserRole(): String?
}