package ru.cookedapp.trckr.core.testHelpers

import ru.cookedapp.trckr.core.converter.ParameterConverter

class FakeParameterConverter(
    private val convertedValue: Any,
    private val predicate: (String, String, Any?) -> Boolean = { _, _, _ -> true },
) : ParameterConverter {

    override fun convert(eventName: String, parameterName: String, value: Any?): Any? {
        return if (predicate(eventName, parameterName, value)) convertedValue else null
    }
}
