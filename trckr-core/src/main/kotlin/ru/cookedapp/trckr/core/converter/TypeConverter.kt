package ru.cookedapp.trckr.core.converter

interface TypeConverter {

    /**
     * @param value value to be converted to string representation
     * @return converted value if conversion successful or null otherwise
     */
    fun convert(value: Any?): Any?
}
