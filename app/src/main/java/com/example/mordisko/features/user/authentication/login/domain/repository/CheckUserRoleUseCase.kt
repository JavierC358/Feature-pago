package com.example.mordisko.features.user.authentication.login.domain.repository

import com.example.mordisko.features.user.authentication.login.domain.repository.UserRepository
import javax.inject.Inject

class CheckUserRoleUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(uid: String): String? {
        return repository.getUserRole(uid)
    }
}