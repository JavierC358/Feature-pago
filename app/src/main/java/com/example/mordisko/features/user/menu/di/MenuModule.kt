package com.example.mordisko.features.user.menu.di

import android.content.Context
import com.example.mordisko.features.admin.data.repository.MenuRepositoryImpl
import com.example.mordisko.features.user.menu.domain.repository.MenuRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MenuModule {

    @Provides
    @Singleton
    fun provideMenuRepository(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context // ✅ Agregado aquí
    ): MenuRepository {
        return MenuRepositoryImpl(firestore, context) // ✅ Ahora se pasan ambos parámetros
    }

}