package ru.cooked.trckr

import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import ru.cooked.trckr.core.ParamConverter
import ru.cooked.trckr.core.PrimitivesConverter
import ru.cooked.trckr.core.TrackerAdapter
import ru.cooked.trckr.core.TrckrInvocationHandler

class TrckrBuilder<T : Any>(private val trackerClass: Class<T>) {

    init {
        if (!trackerClass.isInterface) {
            error("Tracker declaration must be an interface.")
        }
    }

    private val adapters = mutableMapOf<KClass<out TrackerAdapter>, TrackerAdapter>()
    private val converters = mutableMapOf<KClass<out ParamConverter>, ParamConverter>()

    fun addAdapter(adapter: TrackerAdapter) {
        if (adapters.containsKey(adapter::class)) {
            error("Adapter \"${adapter::class.java.simpleName}\" already registered!")
        }
        adapters[adapter::class] = adapter
    }

    fun addConverter(converter: ParamConverter) {
        if (converters.containsKey(converter::class)) {
            error("Converter \"${converter::class.java.simpleName}\" already registered!")
        }
        converters[converter::class] = converter
    }

    @Suppress("Unchecked_Cast")
    internal fun build(): T {
        converters[PrimitivesConverter::class] = PrimitivesConverter()
        return Proxy.newProxyInstance(
            trackerClass.classLoader,
            arrayOf(trackerClass),
            TrckrInvocationHandler(
                adaptersList = adapters.values.toList(),
                converters = converters.values.toList(),
            ),
        ) as T
    }
}