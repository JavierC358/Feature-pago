package com.example.mordisko.features.user.authentication.login.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mordisko.features.user.authentication.login.domain.repository.GoogleAuthRepository
import com.example.mordisko.core.utils.GoogleSignInClientProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GoogleAuthRepository {

    // Necesita contexto para obtener el intent de Google Sign-In
    override fun getGoogleSignInIntent(context: Context): Intent {
        return GoogleSignInClientProvider.getClient(context).signInIntent
    }

    override fun getAccountFromIntent(data: Intent): GoogleSignInAccount? {
        return GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(Exception::class.java)
    }

    override suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Result<AuthResult> {
        return try {
            val credential: AuthCredential =
                GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            Result.success(authResult)
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Error autenticando con Google: ${e.message}", e)
            Result.failure(e)
        }
    }
}