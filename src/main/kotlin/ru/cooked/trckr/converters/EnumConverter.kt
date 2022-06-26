package ru.cooked.trckr.converters

import ru.cooked.trckr.core.GenericParamConverter

class EnumConverter: GenericParamConverter() {

    override fun convert(value: Any?): String? = if (value is Enum<*>) {
        value.name
    } else {
        super.convert(value)
    }
}