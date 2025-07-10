package com.example.mordisko.core.di

import android.content.Context
import com.example.mordisko.features.admin.data.repository.MenuRepositoryImpl
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
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore // 🧩 Añadido para soporte de getUserRole
    ): LoginRepository = LoginRepositoryImpl(firebaseAuth, firestore)

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

    @Provides
    @Singleton
    fun provideMenuRepository(
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): MenuRepositoryImpl = MenuRepositoryImpl(firestore, context)
}