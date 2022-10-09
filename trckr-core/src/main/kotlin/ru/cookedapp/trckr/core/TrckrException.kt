package ru.cookedapp.trckr.core

data class TrckrException(val errorMessage: String) : Throwable(errorMessage)

internal inline fun ensureThat(value: Boolean, messageProvider: () -> String) {
    if (value) return
    throw TrckrException(messageProvider())
}
