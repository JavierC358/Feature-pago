package com.example.mordisko.core.di

import com.example.mordisko.features.user.authentication.login.data.repository.LoginRepositoryImpl
import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLogoutRepository(
        impl: LoginRepositoryImpl
    ): LogoutRepository
}