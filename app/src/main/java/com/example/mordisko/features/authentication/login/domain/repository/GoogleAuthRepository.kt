package com.example.mordisko.features.authentication.login.domain.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult

interface GoogleAuthRepository {

    fun getGoogleSignInIntent(context: Context): Intent

    fun getAccountFromIntent(data: Intent): GoogleSignInAccount?

    suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Result<AuthResult>
}