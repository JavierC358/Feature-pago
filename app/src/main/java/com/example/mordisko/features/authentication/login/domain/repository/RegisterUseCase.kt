package com.example.mordisko.features.authentication.login.domain.repository

import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean {
        return repository.registerWithEmailAndPassword(email, password)
    }
}