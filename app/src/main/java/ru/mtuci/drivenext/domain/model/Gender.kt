package ru.mtuci.drivenext.domain.model

enum class Gender(val dbValue: String) {
    MALE("male"),
    FEMALE("female");

    companion object {
        fun fromDb(value: String?): Gender? = entries.firstOrNull { it.dbValue == value }
    }
}
