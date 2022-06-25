package ru.cooked.trckr.core

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.reflect.KClass
import ru.cooked.trckr.TrckrLogger
import ru.cooked.trckr.TrckrStrictMode
import ru.cooked.trckr.annotations.Event
import ru.cooked.trckr.annotations.Param
import ru.cooked.trckr.annotations.SkipAdapters

class TrckrInvocationHandler(
    private val adapters: List<TrackerAdapter>,
    private val strictMode: TrckrStrictMode,
    private val logger: TrckrLogger?,
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val (event, skippedAdapters) = getMethodAnnotations(method)
        val eventName = event.name.takeIf { it.isNotBlank() } ?: method.name
        val parameters = getParameters(method, args?.toList())
        val suitableAdapters = getSuitableAdapters(method, skippedAdapters?.adapters.orEmpty())

        suitableAdapters.forEach { adapter ->
            adapter.trackEvent(eventName, parameters)
        }
        return Unit
    }

    private fun getMethodAnnotations(method: Method): Pair<Event, SkipAdapters?> {
        val annotations = method.annotations
        val eventAnnotation = annotations.find { it is Event } as? Event
            ?: error("Tracker method \"${method.name}\" missing event annotation.")
        val skipAdaptersAnnotation = annotations.find { it is SkipAdapters } as? SkipAdapters
        return eventAnnotation to skipAdaptersAnnotation
    }

    private fun getParameters(method: Method, args: List<Any>?): Map<String, String> {
        if (method.parameterCount == 0) return emptyMap()
        checkNotNull(args)
        val paramAnnotations = method.parameters.map { parameter ->
            parameter.annotations.find { it is Param } as? Param
                ?: error("Tracker method \"${method.name}\" has parameter with missing @Param annotation.")
        }
        return paramAnnotations.zip(args).associate { (param, value) ->
            param.name to value.toString()
        }
    }

    private fun getSuitableAdapters(
        method: Method,
        skippedAdapters: Array<out KClass<out TrackerAdapter>>,
    ): List<TrackerAdapter> {
        // TODO: check extra skipped adapter
        val suitableAdapters = adapters.filterNot { adapter -> adapter::class in skippedAdapters }
        if (suitableAdapters.isEmpty()) {
            when (strictMode) {
                TrckrStrictMode.WARNING -> {
                    logger?.log("There is no suitable adapters for method \"${method.name}\".")
                }
                TrckrStrictMode.ERROR -> {
                    error("There is no suitable adapters for method \"${method.name}\".")
                }
                TrckrStrictMode.NONE -> Unit
            }
        }
        return suitableAdapters
    }
}