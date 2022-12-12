package ru.cookedapp.trckr.core.converter

internal class PrimitivesConverter : TypeConverter {

    override fun convert(value: Any?): Any? = when (value) {
        null -> "null"
        is String,
        is Char,
        is Number,
        is Boolean -> value
        else -> null
    }
}
