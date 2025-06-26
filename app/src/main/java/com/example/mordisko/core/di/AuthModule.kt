package com.example.mordisko.core.di

import com.example.mordisko.features.user.authentication.login.data.repository.GoogleAuthRepositoryImpl
import com.example.mordisko.features.user.authentication.login.data.repository.LogoutRepositoryImpl
import com.example.mordisko.features.user.authentication.login.data.repository.UserRepositoryImpl
import com.example.mordisko.features.user.authentication.login.domain.repository.GoogleAuthRepository
import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import com.example.mordisko.features.user.authentication.login.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthRepository(
        impl: GoogleAuthRepositoryImpl
    ): GoogleAuthRepository

    @Binds
    @Singleton
    abstract fun bindLogoutRepository(
        impl: LogoutRepositoryImpl
    ): LogoutRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}