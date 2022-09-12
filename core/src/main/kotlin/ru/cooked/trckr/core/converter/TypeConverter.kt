package ru.cooked.trckr.core.converter

interface TypeConverter {

    /**
     * @param value value to be converted to string representation
     * @return string representation of provided value if conversion successful or null otherwise
     */
    fun convert(value: Any?): String?
}
