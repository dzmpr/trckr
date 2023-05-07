package ru.cookedapp.trckr.core

import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.event.TrckrEvent
import ru.cookedapp.trckr.core.exceptions.TrckrConversionException
import ru.cookedapp.trckr.core.param.TrckrParam
import ru.cookedapp.trckr.core.testHelpers.FakeParameterConverter
import ru.cookedapp.trckr.core.testHelpers.FakeTypeConverter
import ru.cookedapp.trckr.core.testHelpers.FirstAdapter
import ru.cookedapp.trckr.core.testHelpers.SecondAdapter
import ru.cookedapp.trckr.core.testHelpers.createFakeAdapter

class TrckrCoreTest {

    private val defaultParameterName = "parameterName"
    private val typeConverterResult = "typeConverter"
    private val parameterConverterResult = "parameterConverter"
    
    @Test
    fun `should track event to all adapters`() {
        testTrackToAdapters(
            skipFirstAdapter = false,
            skipSecondAdapter = false,
            trackToFirstAdapter = true,
            trackToSecondAdapter = true,
        )
    }

    @Test
    fun `should track event to all adapters except skipped adapters`() {
        testTrackToAdapters(
            skipFirstAdapter = true,
            skipSecondAdapter = false,
            trackToFirstAdapter = false,
            trackToSecondAdapter = true,
        )
    }

    @Test
    fun `should not track event to any adapter when all adapters skipped for this event`() {
        testTrackToAdapters(
            skipFirstAdapter = true,
            skipSecondAdapter = true,
            trackToFirstAdapter = false,
            trackToSecondAdapter = false,
        )
    }

    @Test
    fun `should fail when can't find suitable converter for value`() {
        assertFailsWith<TrckrConversionException> {
            testValueConversion(
                parameterConverter = null,
                typeConverter = null,
                convertedValue = null,
            )
        }
    }

    @Test
    fun `should convert value with type converter when suitable type converter found`() {
        testValueConversion(
            parameterConverter = createParameterConverter(),
            typeConverter = createTypeConverter(),
            convertedValue = parameterConverterResult,
        )
    }

    @Test
    fun `should convert value with parameter converter when suitable parameter converter found`() {
        testValueConversion(
            parameterConverter = null,
            typeConverter = createTypeConverter(),
            convertedValue = typeConverterResult,
        )
    }

    @Test
    fun `should convert null parameter value when track strategy is DEFAULT`() {
        testTrackStrategy(
            value = null,
            strategy = TrackStrategy.DEFAULT,
            expectedParameter = defaultParameterName to typeConverterResult,
        )
    }

    @Test
    fun `should track null parameter value when track strategy is TRACK_NULL`() {
        testTrackStrategy(
            value = null,
            strategy = TrackStrategy.TRACK_NULL,
            expectedParameter = defaultParameterName to null,
        )
    }

    @Test
    fun `should skip parameter when parameter value is null and track strategy is SKIP_IF_NULL`() {
        testTrackStrategy(
            value = null,
            strategy = TrackStrategy.SKIP_IF_NULL,
            expectedParameter = null,
        )
    }

    @Test
    fun `should convert not null parameter value when track strategy is DEFAULT`() {
        testTrackStrategy(
            value = "value",
            strategy = TrackStrategy.DEFAULT,
            expectedParameter = defaultParameterName to typeConverterResult,
        )
    }

    @Test
    fun `should convert not null parameter value when track strategy is TRACK_NULL`() {
        testTrackStrategy(
            value = "value",
            strategy = TrackStrategy.TRACK_NULL,
            expectedParameter = defaultParameterName to typeConverterResult,
        )
    }

    @Test
    fun `should convert not null parameter value when track strategy is SKIP_IF_NULL`() {
        testTrackStrategy(
            value = "value",
            strategy = TrackStrategy.SKIP_IF_NULL,
            expectedParameter = defaultParameterName to typeConverterResult,
        )
    }

    private fun testTrackStrategy(
        value: Any?,
        strategy: TrackStrategy,
        expectedParameter: Pair<String, Any?>?,
    ) {
        val adapter = createFakeAdapter()
        val event = createEvent(
            parameters = listOf(TrckrParam(defaultParameterName, strategy, value)),
        )
        val tracker = createTrckrCore(
            adapters = listOf(adapter),
            typeConverters = listOf(createTypeConverter()),
            parameterConverters = emptyList(),
        )

        tracker.track(event)

        val expectedParameters = expectedParameter?.let { mapOf(expectedParameter) } ?: emptyMap()
        val expectedEvents = listOf(event.name to expectedParameters)
        val actualEvents = adapter.getEvents()
        assertEquals(expectedEvents, actualEvents)
    }

    private fun testValueConversion(
        parameterConverter: ParameterConverter?,
        typeConverter: TypeConverter?,
        convertedValue: Any?,
    ) {
        val adapter = createFakeAdapter()
        val parameter = TrckrParam(
            name = defaultParameterName,
            strategy = TrackStrategy.DEFAULT,
            value = "value",
        )
        val event = createEvent(parameters = listOf(parameter))
        val tracker = createTrckrCore(
            adapters = listOf(adapter),
            typeConverters = listOfNotNull(typeConverter),
            parameterConverters = listOfNotNull(parameterConverter),
        )

        tracker.track(event)

        val expectedParameters = mapOf(defaultParameterName to convertedValue)
        val expectedEvents = listOf(event.name to expectedParameters)
        val actualEvents = adapter.getEvents()
        assertEquals(expectedEvents, actualEvents)
    }

    private fun testTrackToAdapters(
        skipFirstAdapter: Boolean,
        skipSecondAdapter: Boolean,
        trackToFirstAdapter: Boolean,
        trackToSecondAdapter: Boolean,
    ) {
        val firstAdapter = createFakeAdapter()
        val firstAdapterImpl = object : TrackerAdapter by firstAdapter, FirstAdapter {}
        val secondAdapter = createFakeAdapter()
        val secondAdapterImpl = object : TrackerAdapter by secondAdapter, SecondAdapter {}
        val event = createEvent(skipAdapters = buildList {
            if (skipFirstAdapter) add(FirstAdapter::class)
            if (skipSecondAdapter) add(SecondAdapter::class)
        })
        val tracker = createTrckrCore(adapters = listOf(firstAdapterImpl, secondAdapterImpl))

        tracker.track(event)

        val expectedEvents = listOf(event.name to emptyMap<String, Any?>())
        val expectedFirstAdaptersEvents = if (trackToFirstAdapter) expectedEvents else emptyList()
        assertEquals(expectedFirstAdaptersEvents, firstAdapter.getEvents())
        val expectedSecondAdapterEvents = if (trackToSecondAdapter) expectedEvents else emptyList()
        assertEquals(expectedSecondAdapterEvents, secondAdapter.getEvents())
    }

    private fun createTypeConverter() = FakeTypeConverter(typeConverterResult)

    private fun createParameterConverter() = FakeParameterConverter(parameterConverterResult)

    private fun createEvent(
        skipAdapters: List<KClass<out TrackerAdapter>> = emptyList(),
        parameters: List<TrckrParam> = emptyList(),
    ) = TrckrEvent(
        name = "event",
        skipAdapters = skipAdapters,
        parameters = parameters,
    )

    private fun createTrckrCore(
        adapters: List<TrackerAdapter> = emptyList(),
        typeConverters: List<TypeConverter> = emptyList(),
        parameterConverters: List<ParameterConverter> = emptyList(),
    ) = TrckrCoreImpl(adapters, typeConverters, parameterConverters)
}
