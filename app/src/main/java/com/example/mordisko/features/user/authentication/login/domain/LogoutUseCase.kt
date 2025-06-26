package com.example.mordisko.features.user.authentication.login.domain

import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: LogoutRepository
) {
    operator fun invoke() {
        repository.logout()
    }
}