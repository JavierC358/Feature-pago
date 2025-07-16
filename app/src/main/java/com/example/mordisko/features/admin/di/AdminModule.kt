package com.example.mordisko.features.admin.di

import com.example.mordisko.features.admin.data.repository.AdminMenuRepositoryImpl
import com.example.mordisko.features.admin.domain.AdminMenuRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminModule {

    @Provides
    @Singleton
    fun provideAdminMenuRepository(
        firestore: FirebaseFirestore
    ): AdminMenuRepository {
        return AdminMenuRepositoryImpl(firestore)
    }
}