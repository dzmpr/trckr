package ru.cookedapp.trckr.core

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.event.TrckrEvent
import ru.cookedapp.trckr.core.event.TrckrEventFactory
import ru.cookedapp.trckr.core.event.createEvent

internal class TrckrInvocationHandler(
    private val adapters: List<TrackerAdapter>,
    private val typeConverters: List<TypeConverter>,
    private val parameterConverters: List<ParameterConverter>,
) : InvocationHandler {

    private val factories = ConcurrentHashMap<Method, TrckrEventFactory>().withDefault { method ->
        TrckrEventFactory.getFactory(method)
    }

    override fun invoke(proxy: Any, method: Method, arguments: Array<out Any?>?): Any {
        val factory = factories.getValue(method)
        val event = factory.createEvent(arguments, typeConverters, parameterConverters)
        trackToSuitableAdapters(factory.skippedAdapters, event)
        return Unit
    }

    private fun trackToSuitableAdapters(
        skippedAdapters: Set<KClass<out TrackerAdapter>>,
        event: TrckrEvent,
    ) {
        adapters.forEach { adapter ->
            if (isAdapterSkipped(adapter, skippedAdapters)) return@forEach
            adapter.trackEvent(event.name, event.parameters)
        }
    }

    private fun isAdapterSkipped(
        adapter: TrackerAdapter,
        skippedAdapters: Set<KClass<out TrackerAdapter>>,
    ): Boolean = skippedAdapters.any { skippedAdapterClass ->
        skippedAdapterClass.isInstance(adapter)
    }
}
