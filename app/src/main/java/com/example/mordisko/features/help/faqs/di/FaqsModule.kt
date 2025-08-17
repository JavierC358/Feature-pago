package com.example.mordisko.features.help.faqs.di

import com.example.mordisko.features.help.faqs.data.FaqsRepositoryImpl
import com.example.mordisko.features.help.faqs.domain.FaqsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FaqsModule {
    @Binds
    @Singleton
    abstract fun bindFaqsRepository(impl: FaqsRepositoryImpl): FaqsRepository
}