package ru.cooked.trckr.core

abstract class GenericParamConverter : ParamConverter {

    override fun convert(eventName: String, paramName: String, value: Any?): String? =
        convert(value)

    open fun convert(value: Any?): String? = null
}