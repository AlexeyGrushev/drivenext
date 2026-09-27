package ru.mtuci.drivenext.presentation

import ru.mtuci.drivenext.domain.model.StartDestination

interface Navigator {
    fun navigate(destination: StartDestination)
}
