package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.event.TrckrEvent
import ru.cookedapp.trckr.core.exceptions.TrckrConversionException
import ru.cookedapp.trckr.core.param.TrckrParam

/**
 * Class that holds all tracking logic.
 *
 * @param adapters Adapters that used to track events.
 * @param typeConverters Type converters to convert parameter values.
 * @param parameterConverters Parameter converters to convert parameter values.
 *
 * @see TrackerAdapter
 * @see TypeConverter
 * @see ParameterConverter
 */
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

    /**
     * This method build map of event parameters.
     *
     * @param eventName Name of event.
     * @param parameters Parameters list that should be converted to map.
     */
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

    /**
     * Convert parameter value using [parameterConverters] and [typeConverters].
     *
     * Conversion has two stages:
     * 1. [Parameter converters][parameterConverters] used to convert value.
     * 2. [Type converters][typeConverters] used to convert value.
     *
     * When all converters return `null` this method throw [TrckrConversionException]
     * which indicates that conversion is unsuccessful.
     *
     * @exception TrckrConversionException
     * @see ParameterConverter
     * @see TypeConverter
     */
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

/**
 * Public method to create [TrckrCore] in DSL style.
 *
 * There is no another way to create [TrckrCoreImpl].
 *
 * @param builder Builder lambda that used to configure [TrckrCore].
 */
fun createTrckr(builder: TrckrBuilder.() -> Unit): TrckrCore =
    TrckrBuilder().apply(builder).build()
