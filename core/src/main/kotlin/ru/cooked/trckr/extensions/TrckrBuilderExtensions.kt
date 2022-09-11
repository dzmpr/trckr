package ru.cooked.trckr.extensions

import ru.cooked.trckr.TrckrBuilder
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.converter.ParamConverter

fun <T : Any> TrckrBuilder<T>.addConverters(vararg converters: ParamConverter) {
    converters.forEach { converter -> addConverter(converter) }
}

fun <T : Any> TrckrBuilder<T>.addAdapters(vararg adapters: TrackerAdapter) {
    adapters.forEach { adapter -> addAdapter(adapter) }
}
