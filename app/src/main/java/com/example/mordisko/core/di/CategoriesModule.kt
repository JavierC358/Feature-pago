package com.example.mordisko.core.di

import com.example.mordisko.features.admin.categories.data.FirestoreCategoriesRepository
import com.example.mordisko.features.admin.categories.domain.CategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesModule {

    @Binds
    @Singleton
    abstract fun bindCategoriesRepository(
        impl: FirestoreCategoriesRepository
    ): CategoriesRepository
}