package com.example.mordisko.core.core_di

import com.example.mordisko.features.user.authentication.login.data.repository.LoginRepositoryImpl
import com.example.mordisko.features.user.authentication.login.domain.LogoutUseCase
import com.example.mordisko.features.user.authentication.login.domain.repository.LoginRepository
import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import com.example.mordisko.features.user.cart.data.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance() // 👈 SOLUCIÓN AQUÍ

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth
    ): LoginRepository = LoginRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        logoutRepository: LogoutRepository
    ): LogoutUseCase = LogoutUseCase(logoutRepository)

    @Provides
    @Singleton
    fun provideOrderRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): OrderRepository = OrderRepository(firestore, firebaseAuth)
}
