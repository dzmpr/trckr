package ru.cookedapp.trckr.core.converter

/**
 * Default [TypeConverter] that will be added as last type converter when creating generated
 * tracker.
 *
 * It converts `null` to "null" and leaves [String], [Char], [Number] and [Boolean]
 * values as is.
 *
 * @see TypeConverter
 */
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
