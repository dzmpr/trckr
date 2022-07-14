package ru.cooked.trckr

import kotlin.test.assertFails
import org.junit.jupiter.api.Test

internal class TrckrBuilderTest {

    @Test
    fun `should fail when provided class is not an interface`() {
        assertFails {
            Trckr.new(this::class.java) {}
        }
    }

    @Test
    fun `should not fail when provided class is interface`() {
        Trckr.new(TestTracker::class.java) {}
    }

    @Test
    fun `should fail when trying to register same adapter for second time`() {
        val adapter = TestAdapter()

        assertFails {
            Trckr.new(TestTracker::class.java) {
                addAdapter(adapter)
                addAdapter(adapter)
            }
        }
    }

    @Test
    fun `should fail when trying to register same converter for second time`() {
        val converter = TestConverter()

        assertFails {
            Trckr.new(TestTracker::class.java) {
                addConverter(converter)
                addConverter(converter)
            }
        }
    }

    @Test
    fun `should fail when at least one of interface methods has no Event annotation`() {
        assertFails {
            Trckr.new(IncompleteEventTestTracker::class.java) {}
        }
    }

    @Test
    fun `should fail when at least one of method parameters has no Param annotation`() {
        assertFails {
            Trckr.new(IncompleteParamTestTracker::class.java) {}
        }
    }

    @Test
    fun `should fail when at least one of interface method has return type other than Unit`() {
        assertFails {
            Trckr.new(TestTrackerWithNonUnitReturnType::class.java) {}
        }
    }
}