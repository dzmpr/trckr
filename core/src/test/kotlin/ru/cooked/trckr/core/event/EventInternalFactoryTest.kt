package ru.cooked.trckr.core.event

import io.mockk.every
import io.mockk.mockk
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import ru.cooked.trckr.TestAdapter
import ru.cooked.trckr.core.TrckrException
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.annotations.SkipAdapters
import ru.cooked.trckr.core.annotations.SkipIfNull
import ru.cooked.trckr.core.annotations.TrackNull
import ru.cooked.trckr.core.converter.ParameterConverter
import ru.cooked.trckr.core.converter.PrimitivesConverter
import ru.cooked.trckr.core.converter.TypeConverter
import ru.cooked.trckr.core.param.TrckrParam

class EventInternalFactoryTest {

    private lateinit var methodMock: Method

    private val defaultMethodName = "methodName"
    private val defaultParameterName = "parameterName"

    @BeforeTest
    fun setup() {
        methodMock = mockk()
    }

    @Test
    fun `should create factory with event name from annotation when it's presents`() {
        val annotationName = "Open app"
        testEventName(annotationName = annotationName, expectedEventName = annotationName)
    }

    @Test
    fun `should create factory with method name as event name when annotation name is empty`() {
        testEventName(annotationName = "", expectedEventName = defaultMethodName)
    }

    @Test
    fun `should create factory with method name as event name when annotation name is blank`() {
        testEventName(annotationName = "   ", expectedEventName = defaultMethodName)
    }

    @Test
    fun `should create factory with skipped adapters classes when method annotated with SkipAdapters annotation`() {
        verifySkippedAdapters(adapters = arrayOf(TestAdapter::class))
    }

    @Test
    fun `should create factory without skipped adapters classes when method has no SkipAdapters annotation`() {
        verifySkippedAdapters(adapters = emptyArray())
    }

    @Test
    fun `should create event factory from method without parameters`() {
        mockMethod()

        val expectedFactory = TrckrEventFactory(
            methodMock.name,
            parameters = emptyList(),
            skippedAdapters = emptySet(),
        )
        val actualFactory = TrckrEventFactory.getFactory(methodMock)

        assertEquals(expectedFactory, actualFactory)
    }

    @Test
    fun `should create event factory from method with parameters`() {
        val parameter = createParameter()
        mockMethod(parameters = arrayOf(parameter))

        val expectedFactory = TrckrEventFactory(
            eventName = methodMock.name,
            parameters = listOf(TrckrParam.createParameter(parameter)),
            skippedAdapters = emptySet(),
        )
        val actualFactory = TrckrEventFactory.getFactory(methodMock)

        assertEquals(expectedFactory, actualFactory)
    }

    @Test
    fun `should create event without parameters from method without parameters`() {
        mockMethod()

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = emptyMap(),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = null,
            typeConverters = emptyList(),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with parameters from method with parameters`() {
        val parameter = createParameter()
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to parameterArgument,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = listOf(PrimitivesConverter()),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event without parameters when method has parameter but it annotated with SkipIfNull and argument is null`() {
        val parameter = createParameter(SkipIfNull())
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = emptyMap(),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(null),
            typeConverters = emptyList(),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with parameter when method has parameter annotated with SkipIfNull and argument not null`() {
        val parameter = createParameter(SkipIfNull())
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to parameterArgument
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = listOf(PrimitivesConverter()),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with null parameter argument when method has parameter annotated with TrackNull and argument is null`() {
        val parameter = createParameter(TrackNull())
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to null,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(null),
            typeConverters = emptyList(),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with parameter when method has parameter annotated with TrackNull but argument not null`() {
        val parameter = createParameter(TrackNull())
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to parameterArgument,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = listOf(PrimitivesConverter()),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with converted parameter when type converter registered for parameter argument type`() {
        val parameter = createParameter()
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to TestTypeConverter.convertedArgument,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = listOf(TestTypeConverter()),
            parameterConverters = emptyList(),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should create event with converted parameter when parameter converter registered for specific parameter of event`() {
        val parameter = createParameter()
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to TestParameterConverter.convertedArgument,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = emptyList(),
            parameterConverters = listOf(TestParameterConverter(defaultMethodName, defaultParameterName)),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should check parameter converters before type converters when converting parameter argument`() {
        val parameter = createParameter()
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        val expectedEvent = TrckrEvent(
            name = defaultMethodName,
            parameters = mapOf(
                defaultParameterName to TestParameterConverter.convertedArgument,
            ),
        )
        val actualEvent = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = arrayOf(parameterArgument),
            typeConverters = listOf(TestTypeConverter()),
            parameterConverters = listOf(TestParameterConverter(defaultMethodName, defaultParameterName)),
        )

        assertEquals(expectedEvent, actualEvent)
    }

    @Test
    fun `should throw exception when no suitable converters found for parameter argument`() {
        val parameter = createParameter()
        val parameterArgument = "argument"
        mockMethod(parameters = arrayOf(parameter))

        assertFailsWith<TrckrException> {
            TrckrEventFactory.getFactory(methodMock).createEvent(
                arguments = arrayOf(parameterArgument),
                typeConverters = emptyList(),
                parameterConverters = emptyList(),
            )
        }
    }

    private fun verifySkippedAdapters(
        adapters: Array<KClass<out TrackerAdapter>>,
    ) {
        mockMethod(skippedAdapters = adapters)

        val expectedFactory = TrckrEventFactory(
            eventName = defaultMethodName,
            parameters = emptyList(),
            skippedAdapters = adapters.toSet(),
        )
        val actualFactory = TrckrEventFactory.getFactory(methodMock)
        assertEquals(expectedFactory, actualFactory)
    }

    private fun testEventName(
        annotationName: String,
        expectedEventName: String,
    ) {
        mockMethod(annotationName)

        val event = TrckrEventFactory.getFactory(methodMock).createEvent(
            arguments = emptyList(),
            typeConverters = emptyList(),
            parameterConverters = emptyList(),
        )
        assertEquals(expectedEventName, event.name)
    }

    private fun createParameter(
        vararg annotations: Annotation,
    ) = mockk<Parameter>().also { mock ->
        every { mock.annotations } returns arrayOf(Param(defaultParameterName), *annotations)
    }

    private fun mockMethod(
        annotationName: String = "",
        skippedAdapters: Array<KClass<out TrackerAdapter>> = emptyArray(),
        parameters: Array<Parameter> = emptyArray(),
    ) {
        val annotations = buildList {
            add(Event(annotationName))
            if (skippedAdapters.isNotEmpty()) {
                add(SkipAdapters(*skippedAdapters))
            }
        }.toTypedArray()
        every { methodMock.annotations } returns annotations
        every { methodMock.name } returns defaultMethodName
        every { methodMock.parameters } returns parameters
    }

    private class TestTypeConverter : TypeConverter {

        override fun convert(value: Any?): Any? {
            return if (value is String) convertedArgument else null
        }

        companion object {

            const val convertedArgument = "typeConvertedArgument"
        }
    }

    private class TestParameterConverter(
        private val eventName: String,
        private val parameterName: String,
    ) : ParameterConverter {

        override fun convert(eventName: String, parameterName: String, value: Any?): Any? {
            return if (eventName == this.eventName &&
                parameterName == this.parameterName &&
                value is String
            ) convertedArgument else null
        }

        companion object {

            const val convertedArgument = "parameterConvertedArgument"
        }
    }
}
