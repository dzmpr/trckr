package ru.cooked.trckr.core

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.annotations.SkipAdapters
import ru.cooked.trckr.core.converter.PrimitivesConverter

class TrckrInvocationHandlerTest {
    
    private lateinit var methodMock: Method
    
    private val defaultMethodName = "methodName"
    private val defaultParameterName = "parameterName"
    private val defaultParameterArgument = "parameterArgument"
    
    @BeforeTest
    fun setup() {
        methodMock = mockk()
    }
    
    @Test
    fun `should track event to all adapters`() {
        val firstAdapter = mockk<FirstAdapter>()
        val secondAdapter = mockk<SecondAdapter>()
        every { firstAdapter.trackEvent(any(), any()) } just Runs
        every { secondAdapter.trackEvent(any(), any()) } just Runs
        mockMethod(skippedAdapters = emptyArray())

        createInvocationHandler(adaptersList = listOf(firstAdapter, secondAdapter)).apply {
            invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }

        val expectedParameters = mapOf(
            defaultParameterName to defaultParameterArgument,
        )
        verify { firstAdapter.trackEvent(defaultMethodName, expectedParameters) }
        verify { secondAdapter.trackEvent(defaultMethodName, expectedParameters) }
    }
    
    @Test
    fun `should track event to adapters except adapters listed in SkipAdapters annotation`() {
        val firstAdapter = mockk<FirstAdapter>()
        val secondAdapter = mockk<SecondAdapter>()
        every { firstAdapter.trackEvent(any(), any()) } just Runs
        every { secondAdapter.trackEvent(any(), any()) } just Runs
        mockMethod(skippedAdapters = arrayOf(secondAdapter::class))

        createInvocationHandler(adaptersList = listOf(firstAdapter, secondAdapter)).apply {
            invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }

        val expectedParameters = mapOf(
            defaultParameterName to defaultParameterArgument,
        )
        verify { firstAdapter.trackEvent(defaultMethodName, expectedParameters) }
        verify(exactly = 0) { secondAdapter.trackEvent(defaultMethodName, expectedParameters) }
    }

    @Test
    fun `should fail to track event when there are no suitable adapters for event`() {
        val firstAdapter = mockk<FirstAdapter>()
        val secondAdapter = mockk<SecondAdapter>()
        mockMethod(skippedAdapters = arrayOf(firstAdapter::class, secondAdapter::class))

        val handler = createInvocationHandler(adaptersList = listOf(firstAdapter, secondAdapter))
        assertFailsWith<TrckrException> {
            handler.invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }
    }

    @Test
    fun `should fail to track event when it tries to skip adapter that is not registered in handler`() {
        val firstAdapter = mockk<FirstAdapter>()
        mockMethod(skippedAdapters = arrayOf(SecondAdapter::class))

        val handler = createInvocationHandler(adaptersList = listOf(firstAdapter))
        assertFailsWith<TrckrException> {
            handler.invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }
    }

    private fun mockMethod(
        skippedAdapters: Array<KClass<out TrackerAdapter>> = emptyArray(),
    ) {
        val annotations = buildList {
            add(Event(name = ""))
            if (skippedAdapters.isNotEmpty()) {
                add(SkipAdapters(*skippedAdapters))
            }
        }.toTypedArray()
        every { methodMock.name } returns defaultMethodName
        every { methodMock.annotations } returns annotations
        every { methodMock.parameters } returns arrayOf(createParameter())
    }

    private fun createParameter() = mockk<Parameter>().also { mock ->
        every { mock.annotations } returns arrayOf(Param(defaultParameterName))
    }
    
    private fun createInvocationHandler(
        adaptersList: List<TrackerAdapter> = emptyList(),
    ) = TrckrInvocationHandler(
        adaptersList,
        typeConverters = listOf(PrimitivesConverter()),
        parameterConverters = emptyList(),
    )

    interface FirstAdapter : TrackerAdapter

    interface SecondAdapter : TrackerAdapter
}
