package com.example.mordisko.features.help.faqs.domain

import com.example.mordisko.features.help.faqs.domain.model.Faq
import kotlinx.coroutines.flow.Flow

interface FaqsRepository {
    /** Observa FAQs activas, ordenadas por 'order' asc. */
    fun observeFaqs(): Flow<List<Faq>>
}