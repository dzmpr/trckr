package ru.cookedapp.trckr.core.extensions

import ru.cookedapp.trckr.core.TrckrBuilder
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter

/**
 * Adds adapters that will be used by **trckr**.
 *
 * @param adapters [TrackerAdapter]'s which will be used to track events.
 *
 * @see TrackerAdapter
 */
fun TrckrBuilder.addAdapters(vararg adapters: TrackerAdapter) {
    adapters.forEach { adapter -> addAdapter(adapter) }
}

/**
 * Adds type converters that will be used by **trckr** to perform conversion.
 *
 * @param converters [TypeConverter]'s which will be used to convert parameter values.
 *
 * @see TypeConverter
 */
fun TrckrBuilder.addConverters(vararg converters: TypeConverter) {
    converters.forEach { converter -> addTypeConverter(converter) }
}

/**
 * Adds parameter converters that will be used by **trckr** to perform conversion.
 *
 * @param converters [ParameterConverter]'s which will be used to convert parameter values.
 *
 * @see ParameterConverter
 */
fun TrckrBuilder.addConverters(vararg converters: ParameterConverter) {
    converters.forEach { converter -> addParameterConverter(converter) }
}
