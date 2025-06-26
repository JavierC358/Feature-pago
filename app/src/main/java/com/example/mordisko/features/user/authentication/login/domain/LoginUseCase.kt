package com.example.mordisko.features.user.authentication.login.domain


import com.example.mordisko.features.user.authentication.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean {
        return repository.loginWithEmailAndPassword(email, password)
    }
}