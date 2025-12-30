package com.example.mordisko.core.utils

import android.content.Context
import com.example.mordisko.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleSignInClientProvider {

    fun getClient(context: Context) =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
            .let { options ->
                GoogleSignIn.getClient(context, options)
            }
}