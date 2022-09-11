package ru.cooked.trckr.core.event

import java.lang.reflect.Method
import kotlin.reflect.KClass
import ru.cooked.trckr.core.TrckrException
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.SkipAdapters
import ru.cooked.trckr.core.converter.ParamConverter
import ru.cooked.trckr.core.param.TrckrParam
import ru.cooked.trckr.extensions.findAnnotation
import ru.cooked.trckr.extensions.getAnnotation

internal class EventInternalFactory private constructor(
    private val eventName: String,
    private val parameters: List<TrckrParam>,
    val skippedAdapters: Set<KClass<out TrackerAdapter>>,
) {

    fun createEvent(arguments: List<Any?>, converters: List<ParamConverter>): TrckrEvent {
        val parameters = buildMap {
            parameters.forEachIndexed { index, parameter ->
                val argument = arguments[index]
                if (argument == null && parameter.skipIfNull) return@forEachIndexed
                put(parameter.name, convertValueToString(arguments[index], parameter.name, converters))
            }
        }
        return TrckrEvent(eventName, parameters)
    }

    private fun convertValueToString(
        value: Any?,
        paramName: String,
        converters: List<ParamConverter>,
    ) = converters.firstNotNullOfOrNull { converter ->
        converter.convert(eventName, paramName, value)
    } ?: throw TrckrException("Can't convert value \"$value\" to string, no suitable converter found!")

    companion object {

        operator fun invoke(method: Method): EventInternalFactory {
            val event = method.annotations.getAnnotation<Event>()
            val eventName = event.name.takeIf { it.isNotBlank() } ?: method.name

            val parameters = getParameters(method)

            val skipAdapters = method.annotations.findAnnotation<SkipAdapters>()
            val skippedAdapters = getSkippedAdapters(skipAdapters)

            return EventInternalFactory(eventName, parameters, skippedAdapters)
        }

        private fun getParameters(
            method: Method,
        ) = method.parameters.map { parameter ->
            TrckrParam.createParameter(parameter)
        }

        private fun getSkippedAdapters(skipAdapters: SkipAdapters?) =
            skipAdapters?.adapters?.toSet().orEmpty()
    }
}

internal fun EventInternalFactory.createEvent(
    arguments: Array<out Any>?,
    converters: List<ParamConverter>,
) = createEvent(arguments.orEmpty().toList(), converters)
