package com.example.mordisko.features.help.faqs.data

import com.example.mordisko.features.help.faqs.domain.FaqsRepository
import com.example.mordisko.features.help.faqs.domain.model.Faq
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FaqsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FaqsRepository {

    override fun observeFaqs(): Flow<List<Faq>> = callbackFlow {
        val reg = firestore.collection("faqs")
            .whereEqualTo("active", true)
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList()) // en error, manda vacío (opcional: log)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { doc ->
                    Faq(
                        id = doc.id,
                        question = doc.getString("question") ?: "",
                        answer = doc.getString("answer") ?: "",
                        order = (doc.getLong("order") ?: 0L).toInt(),
                        active = doc.getBoolean("active") ?: true
                    )
                }.orEmpty()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
}