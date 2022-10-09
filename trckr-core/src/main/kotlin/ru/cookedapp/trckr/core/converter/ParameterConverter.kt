package ru.cookedapp.trckr.core.converter

interface ParameterConverter {

    /**
     * @param eventName name of event for which this conversion performed
     * @param parameterName name of param for which this conversion performed
     * @param value value to be converted to string representation
     * @return converted value if conversion successful or null otherwise
     */
    fun convert(eventName: String, parameterName: String, value: Any?): Any?
}
