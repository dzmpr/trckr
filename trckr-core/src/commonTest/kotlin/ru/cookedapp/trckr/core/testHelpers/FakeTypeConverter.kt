package ru.cookedapp.trckr.core.testHelpers

import ru.cookedapp.trckr.core.converter.TypeConverter

class FakeTypeConverter(
    private val convertedValue: Any,
    private val predicate: (Any?) -> Boolean = { true },
) : TypeConverter {

    override fun convert(value: Any?): Any? {
        return if (predicate(value)) convertedValue else null
    }
}
