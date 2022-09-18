package ru.cooked.trckr.core.converter

internal class PrimitivesConverter : TypeConverter {

    override fun convert(value: Any?): Any? = when (value) {
        null -> "null"
        is String,
        is Long,
        is Int,
        is Float,
        is Double,
        is Boolean -> value
        else -> null
    }
}
