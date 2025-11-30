package ru.vityaman.detekt.sqm.core

object Log {
    private const val IS_ACTIVE: Boolean = true

    fun debug(message: () -> String) {
        if (IS_ACTIVE) {
            println("[SQM] ${message()}")
        }
    }
}
