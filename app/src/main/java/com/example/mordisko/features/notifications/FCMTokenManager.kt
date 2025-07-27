package com.example.mordisko.features.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class FCMTokenManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun saveAdminTokenToFirestore() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                val data = mapOf("fcmToken" to token)
                firestore.collection("users").document(uid)
                    .update(data)
            }
    }
}