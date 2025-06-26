package com.example.mordisko.features.user.authentication.login.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.example.mordisko.features.user.authentication.login.domain.repository.GoogleAuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: GoogleAuthRepository
) {
    suspend fun signInWithGoogle(account: GoogleSignInAccount, activity: Activity): Result<AuthResult> {
        return repository.firebaseAuthWithGoogle(account)
    }

    // 👇 Este operador te permite llamar directamente: googleSignInUseCase(account, activity)
    suspend operator fun invoke(account: GoogleSignInAccount, activity: Activity): Result<AuthResult> {
        return signInWithGoogle(account, activity)
    }


    fun getSignInIntent(context: Context): Intent {
        return repository.getGoogleSignInIntent(context)
    }

    fun getSignedInAccountFromIntent(data: Intent): GoogleSignInAccount? {
        return repository.getAccountFromIntent(data)
    }
}
