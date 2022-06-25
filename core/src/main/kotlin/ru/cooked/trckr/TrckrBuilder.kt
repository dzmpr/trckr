package ru.cooked.trckr

import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import ru.cooked.trckr.core.TrackerAdapter
import ru.cooked.trckr.core.TrckrInvocationHandler

class TrckrBuilder<T : Any>(private val trackerClass: Class<T>) {

    init {
        if (!trackerClass.isInterface) {
            error("Tracker declaration must be an interface.")
        }
    }

    private val adapters = mutableMapOf<KClass<out TrackerAdapter>, TrackerAdapter>()

    fun addAdapter(adapter: TrackerAdapter) {
        if (adapters.containsKey(adapter::class)) {
            error("Adapter \"${adapter::class.java.simpleName}\" already registered!")
        }
        adapters[adapter::class] = adapter
    }

    @Suppress("Unchecked_Cast")
    internal fun build(): T = Proxy.newProxyInstance(
        trackerClass.classLoader,
        arrayOf(trackerClass),
        TrckrInvocationHandler(adapters.values.toList()),
    ) as T
}