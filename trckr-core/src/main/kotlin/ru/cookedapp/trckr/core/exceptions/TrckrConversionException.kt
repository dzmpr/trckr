package ru.cookedapp.trckr.core.exceptions

class TrckrConversionException(
    private val eventName: String,
    private val parameterName: String,
    private val value: Any?,
) : TrckrException() {

    override fun toString(): String {
        return "Can't convert value: \"$value\". Parameter \"$parameterName\" of \"$eventName\" event."
    }
}
