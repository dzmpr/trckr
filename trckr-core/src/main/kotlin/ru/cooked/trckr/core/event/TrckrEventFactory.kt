package ru.cooked.trckr.core.event

import java.lang.reflect.Method
import kotlin.reflect.KClass
import ru.cooked.trckr.core.TrckrException
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.SkipAdapters
import ru.cooked.trckr.core.converter.ParameterConverter
import ru.cooked.trckr.core.converter.TypeConverter
import ru.cooked.trckr.core.extensions.findAnnotation
import ru.cooked.trckr.core.extensions.getAnnotation
import ru.cooked.trckr.core.param.TrckrParam

internal data class TrckrEventFactory constructor(
    private val eventName: String,
    private val parameters: List<TrckrParam>,
    val skippedAdapters: Set<KClass<out TrackerAdapter>>,
) {

    fun createEvent(
        arguments: List<Any?>,
        typeConverters: List<TypeConverter>,
        parameterConverters: List<ParameterConverter>,
    ): TrckrEvent {
        val eventParameters = buildMap {
            parameters.forEachIndexed { index, parameter ->
                val argument = arguments[index]
                if (argument == null) {
                    if (parameter.skipIfNull) return@forEachIndexed
                    if (parameter.trackNull) {
                        put(parameter.name, null)
                        return@forEachIndexed
                    }
                }
                val value = convertValue(argument, parameter.name, typeConverters, parameterConverters)
                put(parameter.name, value)
            }
        }
        return TrckrEvent(eventName, eventParameters)
    }

    private fun convertValue(
        value: Any?,
        paramName: String,
        typeConverters: List<TypeConverter>,
        parameterConverters: List<ParameterConverter>,
    ): Any = parameterConverters.firstNotNullOfOrNull { converter ->
        converter.convert(eventName, paramName, value)
    } ?: typeConverters.firstNotNullOfOrNull { converter ->
        converter.convert(value)
    } ?: throw TrckrException("Can't convert value \"$value\" to string, no suitable converter found!")

    companion object {

        fun getFactory(method: Method): TrckrEventFactory {
            val event = method.annotations.getAnnotation<Event>()
            val eventName = event.name.takeIf { it.isNotBlank() } ?: method.name

            val parameters = getParameters(method)

            val skipAdapters = method.annotations.findAnnotation<SkipAdapters>()
            val skippedAdapters = getSkippedAdapters(skipAdapters)

            return TrckrEventFactory(eventName, parameters, skippedAdapters)
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

internal fun TrckrEventFactory.createEvent(
    arguments: Array<out Any?>?,
    typeConverters: List<TypeConverter>,
    parameterConverters: List<ParameterConverter>,
) = createEvent(arguments.orEmpty().toList(), typeConverters, parameterConverters)
