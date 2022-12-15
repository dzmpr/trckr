package ru.cookedapp.trckr.core.converter

/**
 * Parameter converter is used to convert value based on event and parameter names along with
 * the value itself.
 *
 * Converter should be registered when you create a tracker by calling `createExampleTracker
 *  { addConverter(ParameterConverterImpl()) }`.
 *
 * Parameter converters are called at first stage of value conversion, before [type converters][TypeConverter].
 * Conversion process goes on until one of converters return not-null value or all converters at
 * both stages are out.
 *
 * Converters are called by **trckr** in order in which they are were added. So you should
 * make sure more general converters were added after more specific ones.
 *
 * Example converter:
 * ```kotlin
 * class LegacyParameterConverter : ParameterConverter {
 *
 *     fun convert(eventName: String, parameterName: String, value: Any?): Any? {
 *         return if (eventName == LEGACY_EVENT_NAME && parameterName == PARAMETER_NAME) {
 *             value.toString()
 *         } else {
 *             null
 *         }
 *     }
 * }
 * ```
 *
 * @see TypeConverter
 */
interface ParameterConverter {

    /**
     * This method will be called to try to convert parameter value.
     *
     * @param eventName Name of event for which this conversion performing.
     * @param parameterName Name of parameter for which this conversion performing.
     * @param value Value of parameter to be converted.
     * @return Converted value if conversion successful or null otherwise.
     */
    fun convert(eventName: String, parameterName: String, value: Any?): Any?
}
