package com.example.data.model

data class PortfolioItem(
    val id: String,
    val titleAmharic: String,
    val titleEnglish: String,
    val category: ServiceType,
    val tag: String,
    val imageUrl: String,
    val clientName: String,
    val rating: Float = 4.9f,
    val descriptionAmharic: String
)
