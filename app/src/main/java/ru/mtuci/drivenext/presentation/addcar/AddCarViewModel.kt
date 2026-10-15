package ru.mtuci.drivenext.presentation.addcar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.NewCar
import ru.mtuci.drivenext.domain.usecase.SaveCarUseCase
import ru.mtuci.drivenext.domain.usecase.UploadCarPhotosUseCase

sealed interface AddCarEvent {
    data object InfoSaved : AddCarEvent
    data object CarAdded : AddCarEvent
    data class Error(val message: String) : AddCarEvent
}

data class InfoErrors(
    val yearInvalid: Boolean = false,
    val mileageInvalid: Boolean = false,
    val priceInvalid: Boolean = false,
) {
    val hasErrors get() = yearInvalid || mileageInvalid || priceInvalid
}

/**
 * Одна ViewModel на все шаги добавления автомобиля (общая для Activity):
 * введённые данные не теряются при переходах «Назад» и «Далее».
 * События навигации идут через Channel, а не через флаги в состоянии, чтобы после возврата
 * на предыдущий шаг экран не перескакивал вперёд повторно.
 */
class AddCarViewModel(
    private val saveCar: SaveCarUseCase,
    private val uploadCarPhotos: UploadCarPhotosUseCase,
) : ViewModel() {

    // Шаг 1
    var address = ""

    // Шаг 2
    var year = ""
    var brand = ""
    var model = ""
    var transmission: String? = null
    var mileage = ""
    var price = ""
    var description = ""

    private var createdCarId: Long? = null

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _events = Channel<AddCarEvent>(Channel.BUFFERED)
    val events: Flow<AddCarEvent> = _events.receiveAsFlow()

    /** Начало нового добавления: прошлые данные очищаются. */
    fun reset() {
        address = ""
        year = ""
        brand = ""
        model = ""
        transmission = null
        mileage = ""
        price = ""
        description = ""
        createdCarId = null
        _photos.value = emptyList()
        _isLoading.value = false
    }

    fun validateInfo(): InfoErrors {
        val maxYear = LocalDate.now().year + 1
        val y = year.toIntOrNull()
        return InfoErrors(
            yearInvalid = y == null || year.length != 4 || y !in MIN_YEAR..maxYear,
            mileageInvalid = mileage.toIntOrNull()?.let { it < 0 } ?: true,
            priceInvalid = price.toIntOrNull()?.let { it !in 1..MAX_PRICE } ?: true,
        )
    }

    /** «Отправить» на втором шаге: данные автомобиля отправляются на сервер. */
    fun submitInfo() {
        val code = transmission ?: return
        if (_isLoading.value) return
        _isLoading.value = true
        val car = NewCar(
            address = address,
            year = year.toInt(),
            brand = brand,
            model = model,
            transmission = code,
            mileage = mileage.toInt(),
            pricePerDay = price.toInt(),
            description = description,
        )
        viewModelScope.launch {
            // Если пользователь вернулся с шага «Фото» и отправил снова — обновляем уже созданный автомобиль
            saveCar(car, createdCarId)
                .onSuccess { id ->
                    createdCarId = id
                    _isLoading.value = false
                    _events.send(AddCarEvent.InfoSaved)
                }
                .onFailure { e ->
                    _isLoading.value = false
                    _events.send(AddCarEvent.Error(e.message.orEmpty()))
                }
        }
    }

    fun addPhotos(uris: List<String>) {
        _photos.value = (_photos.value + uris).distinct().take(MAX_PHOTOS)
    }

    fun removePhoto(uri: String) {
        _photos.value = _photos.value - uri
    }

    /** «Далее» на шаге с фото: загрузка фото в хранилище и привязка к автомобилю. */
    fun submitPhotos() {
        val carId = createdCarId ?: return
        val list = _photos.value
        if (list.isEmpty() || _isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            uploadCarPhotos(carId, list)
                .onSuccess {
                    _isLoading.value = false
                    _events.send(AddCarEvent.CarAdded)
                }
                .onFailure { e ->
                    _isLoading.value = false
                    _events.send(AddCarEvent.Error(e.message.orEmpty()))
                }
        }
    }

    companion object {
        const val MAX_PHOTOS = 5
        private const val MIN_YEAR = 1950
        private const val MAX_PRICE = 1_000_000
    }
}
