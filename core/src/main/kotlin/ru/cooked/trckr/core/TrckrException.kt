package ru.cooked.trckr.core

data class TrckrException(val errorMessage: String) : Throwable(errorMessage)

internal fun trckrError(message: String): Nothing = throw TrckrException(message)
