package com.example.mordisko.features.help.faqs.domain.model

data class Faq(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val order: Int = 0,
    val active: Boolean = true
)