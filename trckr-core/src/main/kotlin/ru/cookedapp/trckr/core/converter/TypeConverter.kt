package ru.cookedapp.trckr.core.converter

/**
 * Type converter is used to convert value based only on the value itself.
 *
 * Converter should be registered when you create a tracker by calling `createExampleTracker
 *  { addConverter(TypeConverterImpl()) }`.
 *
 * Type converters are called at second stage of value conversion, after [parameter converters][ParameterConverter].
 * Conversion process goes on until one of converters return not-null value or all converters at
 * both stages are out.
 *
 * Converters are called by **trckr** in order in which they are were added. So you should
 * make sure more general converters were added after more specific ones.
 *
 * Example converter:
 * ```kotlin
 * class EnumConverter : TypeConverter {
 *
 *     fun convert(value: Any?): Any? {
 *         return if (value is Enum<*>) value.name else null
 *     }
 * }
 * ```
 *
 * @see ParameterConverter
 */
interface TypeConverter {

    /**
     * This method will be called to try to convert parameter value.
     *
     * @param value Value of parameter to be converted.
     * @return Converted value if conversion successful or null otherwise.
     */
    fun convert(value: Any?): Any?
}
