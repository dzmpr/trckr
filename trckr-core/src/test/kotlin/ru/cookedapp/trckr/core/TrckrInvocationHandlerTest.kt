package ru.cookedapp.trckr.core

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import org.junit.jupiter.api.Test
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.SkipAdapters
import ru.cookedapp.trckr.core.converter.PrimitivesConverter

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
        mockMethod(skippedAdapters = arrayOf(SecondAdapter::class))

        createInvocationHandler(adaptersList = listOf(firstAdapter, secondAdapter)).apply {
            invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }

        val expectedParameters = mapOf(
            defaultParameterName to defaultParameterArgument,
        )
        verify { firstAdapter.trackEvent(defaultMethodName, expectedParameters) }
        verify(exactly = 0) { secondAdapter.trackEvent(any(), any()) }
    }

    @Test
    fun `should not track event to any adapter when all registered adapters skipped for this event`() {
        val firstAdapter = mockk<FirstAdapter>()
        val secondAdapter = mockk<SecondAdapter>()
        every { firstAdapter.trackEvent(any(), any()) } just Runs
        every { secondAdapter.trackEvent(any(), any()) } just Runs
        mockMethod(skippedAdapters = arrayOf(FirstAdapter::class, SecondAdapter::class))

        createInvocationHandler(adaptersList = listOf(firstAdapter, secondAdapter)).apply {
            invoke(Unit, methodMock, arguments = arrayOf(defaultParameterArgument))
        }

        verify(exactly = 0) { firstAdapter.trackEvent(any(), any()) }
        verify(exactly = 0) { secondAdapter.trackEvent(any(), any()) }
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
