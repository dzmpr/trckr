package ru.cookedapp.trckr.core

import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import ru.cookedapp.trckr.core.exceptions.TrckrBuilderException

internal class TrckrBuilderTest {

    @Test
    fun `should fail when trying to create tracker without adapters`() {
        assertFailsWith<TrckrBuilderException> {
            TrckrBuilder().build()
        }
    }

    @Test
    fun `should create tracker when at least one adapter added`() {
        val adapter = object : FirstAdapter {
            override fun trackEvent(eventName: String, parameters: Map<String, Any?>) = Unit
        }

        TrckrBuilder().apply {
            addAdapter(adapter)
        }.build()
    }
}
