package com.example.mordisko.features.user.authentication.login.domain

import com.example.mordisko.features.user.authentication.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.loginWithEmailAndPassword(email, password)
    }

    suspend fun getUserRole(): String? = repository.getUserRole()
}