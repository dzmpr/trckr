package ru.cookedapp.trckr.core.extensions

import ru.cookedapp.trckr.core.TrckrBuilder
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter

fun TrckrBuilder.addConverters(vararg converters: TypeConverter) {
    converters.forEach { converter -> addTypeConverter(converter) }
}

fun TrckrBuilder.addConverters(vararg converters: ParameterConverter) {
    converters.forEach { converter -> addParameterConverter(converter) }
}

fun TrckrBuilder.addAdapters(vararg adapters: TrackerAdapter) {
    adapters.forEach { adapter -> addAdapter(adapter) }
}
