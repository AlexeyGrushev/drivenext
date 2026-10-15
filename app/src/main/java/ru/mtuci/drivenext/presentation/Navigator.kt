package ru.mtuci.drivenext.presentation

import ru.mtuci.drivenext.domain.model.StartDestination

/** Навигация между экранами. Реализует MainActivity; фрагменты знают только этот интерфейс. */
interface Navigator {
    /** Переход на стартовый экран, определённый после Splash (без возможности вернуться назад). */
    fun navigate(destination: StartDestination)

    fun openLogin()
    fun openSignUpStep1()
    fun openSignUpStep2()
    fun openSignUpStep3()
    fun openRegistrationSuccess()

    /** Главный экран; стек навигации очищается. */
    fun openHome()

    /** Выход из аккаунта: экран входа, стек очищается. */
    fun openLoginAsRoot()

    /** Оформление аренды выбранного автомобиля (без нижнего меню). */
    fun openCheckout(carId: Long)

    /** «Удачной поездки»: стек навигации очищается. */
    fun openBookingSuccess()

    fun openBookings()

    /** «Помощь»: частые вопросы и связь с поддержкой. */
    fun openHelp()

    /** Список броней после успешного оформления: домой, затем «Мои бронирования» поверх. */
    fun openBookingsAfterSuccess()

    fun openBookingDetails(bookingId: Long)

    /** «Стать арендодателем» и шаги добавления автомобиля. */
    fun openBecomeHost()
    fun openAddCarAddress()
    fun openAddCarInfo()
    fun openCarPhotos()

    /** «Ваш автомобиль добавлен»: стек навигации очищается. */
    fun openCarAdded()

    fun back()
}
