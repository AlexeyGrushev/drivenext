package ru.mtuci.drivenext.domain.model

data class Car(
    val id: Long,
    val brand: String,
    val model: String,
    val pricePerDay: Int,
    val transmission: String,
    val fuel: String,
    val imageUrl: String?,
    val address: String? = null,
    val description: String? = null,
    val year: Int? = null,
    val mileage: Int? = null,
    val photos: List<String> = emptyList(),
) {
    /** Заголовок на экране «Детали»: «Mercedes-Benz S 500 Sedan 2020». */
    val title: String get() = listOfNotNull(brand, model, year?.toString()).joinToString(" ")

    /** Главное фото: первое загруженное владельцем или фото из каталога. */
    val mainImage: String? get() = photos.firstOrNull() ?: imageUrl

    /** Все фото для галереи на экране «Детали»; если загруженных фото нет — одно фото из каталога. */
    val gallery: List<String> get() = photos.ifEmpty { listOfNotNull(imageUrl) }
}
