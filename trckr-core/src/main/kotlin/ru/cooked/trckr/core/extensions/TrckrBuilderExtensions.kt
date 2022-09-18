package ru.cooked.trckr.core.extensions

import ru.cooked.trckr.core.TrckrBuilder
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.converter.ParameterConverter
import ru.cooked.trckr.core.converter.TypeConverter

fun <T : Any> TrckrBuilder<T>.addConverters(vararg converters: TypeConverter) {
    converters.forEach { converter -> addTypeConverter(converter) }
}

fun <T : Any> TrckrBuilder<T>.addConverters(vararg converters: ParameterConverter) {
    converters.forEach { converter -> addParameterConverter(converter) }
}

fun <T : Any> TrckrBuilder<T>.addAdapters(vararg adapters: TrackerAdapter) {
    adapters.forEach { adapter -> addAdapter(adapter) }
}
