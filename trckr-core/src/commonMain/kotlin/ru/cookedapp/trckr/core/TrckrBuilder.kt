package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.PrimitivesConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.exceptions.TrckrBuilderException

/**
 * A builder class that configures [TrckrCore] to use it in generated tracker.
 *
 * You don't need to create this class manually, instead use generated
 * method 'create*TrackerName*'.
 *
 * @see TrckrCore
 */
class TrckrBuilder internal constructor() {

    private val adapters = mutableListOf<TrackerAdapter>()
    private val typeConverters = mutableListOf<TypeConverter>()
    private val parameterConverters = mutableListOf<ParameterConverter>()

    /**
     * Adds an adapter that will be used by tracker.
     *
     * @param adapter [TrackerAdapter] which will be used to track events.
     *
     * @see TrackerAdapter
     */
    fun addAdapter(adapter: TrackerAdapter) {
        adapters.add(adapter)
    }

    /**
     * Adds type converter that will be used by **trckr** to perform conversion.
     *
     * @param converter [TypeConverter] which will be used to convert parameter values.
     *
     * @see TypeConverter
     */
    fun addTypeConverter(converter: TypeConverter) {
        typeConverters.add(converter)
    }

    /**
     * Adds parameter converter that will be used by **trckr** to perform conversion.
     *
     * @param converter [ParameterConverter] which will be used to convert parameter values.
     *
     * @see ParameterConverter
     */
    fun addParameterConverter(converter: ParameterConverter) {
        parameterConverters.add(converter)
    }

    /**
     * This method called to create instance of [TrckrCoreImpl].
     *
     * If no adapters were added [TrckrBuilderException] would be thrown.
     *
     * Note: this method add [primitives][PrimitivesConverter] type converter to convert
     * basic types.
     *
     * @return [TrckrCore] class that be used inside generated tracker.
     * @throws TrckrBuilderException
     *
     * @see PrimitivesConverter
     */
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
