package ru.cooked.trckr.core.converter

fun interface ParamConverter {

    /**
     * @param eventName name of event for which this conversion performed
     * @param paramName name of param for which this conversion performed
     * @param value value to be converted to string representation
     * @return string representation of provided value if conversion successful or null otherwise
     */
    fun convert(eventName: String, paramName: String, value: Any?): String?
}
