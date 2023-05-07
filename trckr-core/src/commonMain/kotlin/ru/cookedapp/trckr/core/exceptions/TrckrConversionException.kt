package ru.cookedapp.trckr.core.exceptions

/**
 * **Trckr** library exception that can be thrown by tracker during conversion process.
 *
 * It indicates that no suitable converters found and one of parameters can't be converted.
 */
class TrckrConversionException(
    private val eventName: String,
    private val parameterName: String,
    private val value: Any?,
) : TrckrException() {

    override fun toString(): String {
        return "Can't convert value: \"$value\". Parameter \"$parameterName\" of \"$eventName\" event."
    }
}
