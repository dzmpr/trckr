package ru.cooked.trckr.core

import java.lang.reflect.Method
import kotlin.reflect.KClass
import ru.cooked.trckr.annotations.Event
import ru.cooked.trckr.annotations.Param
import ru.cooked.trckr.annotations.SkipAdapters

internal class EventInternalFactory private constructor(
    private val eventName: String,
    private val parameterNames: List<String>,
    val skippedAdapters: Set<KClass<out TrackerAdapter>>,
) {

    fun newEvent(arguments: List<Any?>, converters: List<ParamConverter>): EventInternal {
        if (parameterNames.size != arguments.size) {
            error("Incorrect arguments count for event: \"$eventName\".")
        }
        val parameters = buildMap {
            parameterNames.forEachIndexed { index, name ->
                put(name, convertValueToString(arguments[index], name, converters))
            }
        }
        return EventInternal(eventName, parameters)
    }

    private fun convertValueToString(
        value: Any?,
        paramName: String,
        converters: List<ParamConverter>,
    ): String {
        if (value is String) return value
        return converters.firstNotNullOfOrNull { converter ->
            converter.convert(eventName, paramName, value)
        } ?: error("Can't convert value \"$value\" to string, no suitable converter found!")
    }

    companion object {

        operator fun invoke(method: Method): EventInternalFactory {
            val (event, skipAdapters) = getMethodAnnotations(method)
            val eventName = event.name.takeIf { it.isNotBlank() } ?: method.name
            val parameterNames = getParameterNames(method)
            val skippedAdapters = getSkippedAdapters(skipAdapters)

            return EventInternalFactory(eventName, parameterNames, skippedAdapters)
        }

        private fun getMethodAnnotations(method: Method): Pair<Event, SkipAdapters?> {
            val annotations = method.annotations
            val eventAnnotation = annotations.find { it is Event } as? Event
                ?: error("Tracker method \"${method.name}\" missing @Event annotation.")
            val skipAdaptersAnnotation = annotations.find { it is SkipAdapters } as? SkipAdapters
            return eventAnnotation to skipAdaptersAnnotation
        }

        private fun getParameterNames(method: Method): List<String> {
            if (method.parameterCount == 0) return emptyList()
            val paramAnnotations = method.parameters.map { parameter ->
                parameter.annotations.find { it is Param } as? Param
                    ?: error("Tracker method \"${method.name}\" has parameter with missing @Param annotation.")
            }
            return paramAnnotations.map { param -> param.name }
        }

        private fun getSkippedAdapters(skipAdapters: SkipAdapters?) =
            skipAdapters?.adapters?.toSet().orEmpty()
    }
}

internal fun EventInternalFactory.newEvent(
    arguments: Array<out Any>?,
    converters: List<ParamConverter>,
) = newEvent(arguments.orEmpty().toList(), converters)
