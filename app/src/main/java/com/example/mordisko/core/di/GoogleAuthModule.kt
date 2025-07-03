package com.example.mordisko.core.di

import android.content.Context
import com.example.mordisko.features.user.authentication.login.data.repository.GoogleAuthRepositoryImpl
import com.example.mordisko.features.user.authentication.login.domain.repository.GoogleAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleAuthModule {

    @Provides
    @Singleton
    fun provideGoogleAuthRepository(
        firebaseAuth: FirebaseAuth
    ): GoogleAuthRepository = GoogleAuthRepositoryImpl(firebaseAuth)
}