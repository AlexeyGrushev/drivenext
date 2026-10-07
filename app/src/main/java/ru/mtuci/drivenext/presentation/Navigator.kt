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

    fun back()
}
