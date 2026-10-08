package ru.mtuci.drivenext.domain.model

data class Car(
    val id: Long,
    val brand: String,
    val model: String,
    val pricePerDay: Int,
    val transmission: String,
    val fuel: String,
    val imageUrl: String?,
)
