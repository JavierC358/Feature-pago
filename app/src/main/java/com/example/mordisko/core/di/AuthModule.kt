package com.example.mordisko.core.di

import android.content.Context
import com.example.mordisko.features.admin.data.repository.MenuRepositoryImpl
import com.example.mordisko.features.user.authentication.login.data.repository.LoginRepositoryImpl
import com.example.mordisko.features.user.authentication.login.domain.LogoutUseCase
import com.example.mordisko.features.user.authentication.login.domain.repository.LoginRepository
import com.example.mordisko.features.user.authentication.login.domain.repository.LogoutRepository
import com.example.mordisko.features.user.cart.data.repository.OrderRepository
import com.example.mordisko.features.user.profile.data.repository.UserRepositoryImpl
import com.example.mordisko.features.user.profile.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
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

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): UserRepository = UserRepositoryImpl(firestore, storage, auth) // ✅ Con clase correcta
}