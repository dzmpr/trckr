package ru.cookedapp.trckr.core.param

import io.mockk.every
import io.mockk.mockk
import java.lang.reflect.Parameter
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.SkipIfNull
import ru.cookedapp.trckr.core.annotations.TrackNull

class TrckrParamTest {

    private lateinit var parameterMock: Parameter

    private val defaultName = "eventParameter"

    @BeforeTest
    fun setup() {
        parameterMock = mockk()
    }

    @Test
    fun `should create TrckrParam with skipIfNull and trackNull when both annotations present`() {
        mockParameter(hasSkipIfNull = true, hasTrackNull = true)
        val expectedParam = TrckrParam(
            name = defaultName,
            skipIfNull = true,
            trackNull = true,
        )
        val actualParam = TrckrParam.createParameter(parameterMock)

        assertEquals(expectedParam, actualParam)
    }

    @Test
    fun `should create TrckrParam with skipIfNull when only SkipIfNull annotation presents`() {
        mockParameter(hasSkipIfNull = true, hasTrackNull = false)
        val expectedParam = TrckrParam(
            name = defaultName,
            skipIfNull = true,
            trackNull = false,
        )
        val actualParam = TrckrParam.createParameter(parameterMock)

        assertEquals(expectedParam, actualParam)
    }

    @Test
    fun `should create TrckrParam with trackNull when only TrackNull annotation presents`() {
        mockParameter(hasSkipIfNull = false, hasTrackNull = true)
        val expectedParam = TrckrParam(
            name = defaultName,
            skipIfNull = false,
            trackNull = true,
        )
        val actualParam = TrckrParam.createParameter(parameterMock)

        assertEquals(expectedParam, actualParam)
    }

    @Test
    fun `should create TrckrParam without skipIfNull and trackNull when both SkipIfNull and TrackNull annotations not present`() {
        mockParameter(hasSkipIfNull = false, hasTrackNull = false)
        val expectedParam = TrckrParam(
            name = defaultName,
            skipIfNull = false,
            trackNull = false,
        )
        val actualParam = TrckrParam.createParameter(parameterMock)

        assertEquals(expectedParam, actualParam)
    }

    private fun mockParameter(
        hasSkipIfNull: Boolean,
        hasTrackNull: Boolean,
    ) {
        every { parameterMock.annotations } returns createAnnotations(hasSkipIfNull, hasTrackNull)
    }

    private fun createAnnotations(
        hasSkipIfNull: Boolean,
        hasTrackNull: Boolean,
    ) = buildList {
        add(Param(defaultName))
        if (hasSkipIfNull) add(SkipIfNull())
        if (hasTrackNull) add(TrackNull())
    }.toTypedArray()
}
