package ru.cooked.trckr

import kotlin.test.assertFails
import org.junit.jupiter.api.Test

internal class TrckrBuilderTest {

    @Test
    fun `should fail when provided class is not an interface`() {
        class TestClass

        assertFails {
            Trckr.new(TestClass::class.java) {}
        }
    }

    @Test
    fun `should not fail when provided class is interface`() {
        Trckr.new(TestTracker::class.java) {}
    }

    @Test
    fun `should fails when trying to register same adapter for second time`() {
        val adapter = TestAdapter()

        assertFails {
            Trckr.new(TestTracker::class.java) {
                addAdapter(adapter)
                addAdapter(adapter)
            }
        }
    }

    @Test
    fun `should fails when trying to register same converter for second time`() {
        val converter = TestConverter()

        assertFails {
            Trckr.new(TestTracker::class.java) {
                addConverter(converter)
                addConverter(converter)
            }
        }
    }
}