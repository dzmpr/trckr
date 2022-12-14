package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.PrimitivesConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.exceptions.TrckrBuilderException

class TrckrBuilder internal constructor() {

    private val adapters = mutableListOf<TrackerAdapter>()
    private val typeConverters = mutableListOf<TypeConverter>()
    private val parameterConverters = mutableListOf<ParameterConverter>()

    fun addAdapter(adapter: TrackerAdapter) {
        adapters.add(adapter)
    }

    fun addTypeConverter(converter: TypeConverter) {
        typeConverters.add(converter)
    }

    fun addParameterConverter(converter: ParameterConverter) {
        parameterConverters.add(converter)
    }

    internal fun build(): TrckrCore {
        if (adapters.isEmpty()) {
            throw TrckrBuilderException("No adapters registered.")
        }
        return TrckrCoreImpl(
            adapters = adapters,
            typeConverters = typeConverters + PrimitivesConverter(),
            parameterConverters = parameterConverters,
        )
    }
}
