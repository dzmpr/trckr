package ru.cooked.trckr

import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import ru.cooked.trckr.core.TrckrException

internal class TrckrBuilderTest {

    @Test
    fun `should fail when provided class is not an interface`() {
        assertFailsWith<TrckrException> {
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

        assertFailsWith<TrckrException> {
            Trckr.new(TestTracker::class.java) {
                addAdapter(adapter)
                addAdapter(adapter)
            }
        }
    }

    @Test
    fun `should fail when trying to register same converter for second time`() {
        val converter = TestConverter()

        assertFailsWith<TrckrException> {
            Trckr.new(TestTracker::class.java) {
                addConverter(converter)
                addConverter(converter)
            }
        }
    }

    @Test
    fun `should fail when at least one of interface methods has no Event annotation`() {
        assertFailsWith<TrckrException> {
            Trckr.new(IncompleteEventTestTracker::class.java) {}
        }
    }

    @Test
    fun `should fail when at least one of method parameters has no Param annotation`() {
        assertFailsWith<TrckrException> {
            Trckr.new(IncompleteParamTestTracker::class.java) {}
        }
    }

    @Test
    fun `should fail when at least one of interface method has return type other than Unit`() {
        assertFailsWith<TrckrException> {
            Trckr.new(TestTrackerWithNonUnitReturnType::class.java) {}
        }
    }
}