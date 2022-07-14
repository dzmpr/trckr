package ru.cooked.trckr.core.converter

internal class PrimitivesConverter : GenericParamConverter() {

    override fun convert(value: Any?): String? = when (value) {
        null -> "null"
        is String -> value
        is Long,
        is Int,
        is Float,
        is Double,
        is Boolean -> value.toString()
        else -> super.convert(value)
    }
}