package ru.mtuci.drivenext.domain.model

/** Данные нового автомобиля, которые вводит арендодатель (ЛР5). */
data class NewCar(
    val address: String,
    val year: Int,
    val brand: String,
    val model: String,
    val transmission: String,
    val mileage: Int,
    val pricePerDay: Int,
    val description: String,
)
