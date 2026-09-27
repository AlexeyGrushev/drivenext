package ru.mtuci.drivenext.domain.repository

interface NetworkChecker {
    fun isOnline(): Boolean
}
