package ru.cooked.trckr.core

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal class TrckrInvocationHandler(
    adaptersList: List<TrackerAdapter>,
    private val converters: List<ParamConverter>,
) : InvocationHandler {

    private val adapters = adaptersList.associateBy { adapter -> adapter::class }
    private val factories = ConcurrentHashMap<Method, EventInternalFactory>()

    override fun invoke(proxy: Any, method: Method, arguments: Array<out Any>?): Any {
        val factory = factories.getOrPut(method) { EventInternalFactory(method) }
        val event = factory.newEvent(arguments, converters)

        getSuitableAdapters(method, factory.skippedAdapters).forEach { adapter ->
            adapters.getValue(adapter).track(event)
        }
        return Unit
    }

    private fun getSuitableAdapters(
        method: Method,
        skippedAdapters: Set<KClass<out TrackerAdapter>>,
    ): Set<KClass<out TrackerAdapter>> {
        val registeredAdapters = adapters.keys
        val suitableAdapters = registeredAdapters - skippedAdapters
        if (suitableAdapters.isEmpty()) {
            error("There is no suitable adapters for method \"${method.name}\".")
        }
        if ((skippedAdapters - registeredAdapters).isNotEmpty()) {
            error("Method \"${method.name}\" tries to skip adapters that are not registered.")
        }
        return suitableAdapters
    }

    private fun TrackerAdapter.track(event: EventInternal) = trackEvent(event.name, event.parameters)
}