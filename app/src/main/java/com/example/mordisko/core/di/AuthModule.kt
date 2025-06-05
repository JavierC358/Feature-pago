package com.example.mordisko.core.di

import com.example.mordisko.features.authentication.login.data.repository.GoogleAuthRepositoryImpl
import com.example.mordisko.features.authentication.login.data.repository.LogoutRepositoryImpl
import com.example.mordisko.features.authentication.login.domain.repository.GoogleAuthRepository
import com.example.mordisko.features.authentication.login.domain.repository.LogoutRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
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
}