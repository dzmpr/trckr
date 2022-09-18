package ru.cooked.trckr.demo.converters

import ru.cooked.trckr.core.converter.TypeConverter

class EnumConverter : TypeConverter {

    override fun convert(value: Any?): String? = if (value is Enum<*>) value.name else null
}
