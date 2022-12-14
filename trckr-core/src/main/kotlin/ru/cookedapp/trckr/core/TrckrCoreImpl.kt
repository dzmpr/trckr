package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.event.TrckrEvent
import ru.cookedapp.trckr.core.exceptions.TrckrConversionException
import ru.cookedapp.trckr.core.param.TrckrParam

internal class TrckrCoreImpl internal constructor(
    private val adapters: List<TrackerAdapter>,
    private val typeConverters: List<TypeConverter>,
    private val parameterConverters: List<ParameterConverter>,
) : TrckrCore {

    override fun track(event: TrckrEvent) {
        val parameters = getParametersMap(event.name, event.parameters)
        adapters.filter { adapter ->
            event.skipAdapters.none { it.isInstance(adapter) }
        }.forEach { adapter ->
            adapter.trackEvent(event.name, parameters)
        }
    }

    private fun getParametersMap(
        eventName: String,
        parameters: List<TrckrParam>,
    ): Map<String, Any?> = buildMap {
        parameters.forEach { (parameterName, trackStrategy, value) ->
            val convertedValue = if (value == null) {
                when (trackStrategy) {
                    TrackStrategy.DEFAULT -> getConvertedValue(eventName, parameterName, value = null)
                    TrackStrategy.SKIP_IF_NULL -> return@forEach
                    TrackStrategy.TRACK_NULL -> null
                }
            } else {
                getConvertedValue(eventName, parameterName, value)
            }
            put(parameterName, convertedValue)
        }
    }

    private fun getConvertedValue(
        eventName: String,
        parameterName: String,
        value: Any?,
    ): Any = parameterConverters.firstNotNullOfOrNull { converter ->
        converter.convert(eventName, parameterName, value)
    } ?: typeConverters.firstNotNullOfOrNull { converter ->
        converter.convert(value)
    } ?: throw TrckrConversionException(eventName, parameterName, value)
}

fun createTrckr(builder: TrckrBuilder.() -> Unit): TrckrCore =
    TrckrBuilder().apply(builder).build()
