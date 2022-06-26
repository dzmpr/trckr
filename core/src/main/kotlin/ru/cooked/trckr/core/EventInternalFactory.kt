package ru.cooked.trckr.core

import java.lang.reflect.Method
import kotlin.reflect.KClass
import ru.cooked.trckr.annotations.Event
import ru.cooked.trckr.annotations.SkipAdapters

internal class EventInternalFactory private constructor(
    private val eventName: String,
    private val parameters: List<ParamInternal>,
    val skippedAdapters: Set<KClass<out TrackerAdapter>>,
) {

    fun createEvent(arguments: List<Any?>, converters: List<ParamConverter>): EventInternal {
        if (parameters.size != arguments.size) {
            error("Incorrect arguments count for event: \"$eventName\".")
        }
        val parameters = buildMap {
            parameters.forEachIndexed { index, parameter ->
                val argument = arguments[index]
                if (argument == null && parameter.skipIfNull) return@forEachIndexed
                put(parameter.name, convertValueToString(arguments[index], parameter.name, converters))
            }
        }
        return EventInternal(eventName, parameters)
    }

    private fun convertValueToString(
        value: Any?,
        paramName: String,
        converters: List<ParamConverter>,
    ) = converters.firstNotNullOfOrNull { converter ->
        converter.convert(eventName, paramName, value)
    } ?: error("Can't convert value \"$value\" to string, no suitable converter found!")

    companion object {

        operator fun invoke(method: Method): EventInternalFactory {
            val (event, skipAdapters) = getMethodAnnotations(method)
            val eventName = event.name.takeIf { it.isNotBlank() } ?: method.name
            val parameters = getParameters(method)
            val skippedAdapters = getSkippedAdapters(skipAdapters)

            return EventInternalFactory(eventName, parameters, skippedAdapters)
        }

        private fun getMethodAnnotations(method: Method): Pair<Event, SkipAdapters?> {
            val annotations = method.annotations
            val eventAnnotation = annotations.find { it is Event } as? Event
                ?: error("Tracker method \"${method.name}\" missing @Event annotation.")
            val skipAdaptersAnnotation = annotations.find { it is SkipAdapters } as? SkipAdapters
            return eventAnnotation to skipAdaptersAnnotation
        }

        private fun getParameters(
            method: Method,
        ) = if (method.parameterCount != 0) ParamInternal.createParameters(method) else emptyList()

        private fun getSkippedAdapters(skipAdapters: SkipAdapters?) =
            skipAdapters?.adapters?.toSet().orEmpty()
    }
}

internal fun EventInternalFactory.createEvent(
    arguments: Array<out Any>?,
    converters: List<ParamConverter>,
) = createEvent(arguments.orEmpty().toList(), converters)
