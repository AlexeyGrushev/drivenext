package ru.mtuci.drivenext.presentation.cars

import ru.mtuci.drivenext.domain.model.Car

/** Состояние экрана со списком автомобилей: загрузка, данные или ошибка. */
data class CarsUiState(
    val isLoading: Boolean = true,
    val cars: List<Car> = emptyList(),
    val errorMessage: String? = null,
    val isLoaded: Boolean = false,
)
