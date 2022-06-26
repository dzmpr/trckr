package ru.cooked.trckr

import ru.cooked.trckr.core.GenericParamConverter
import ru.cooked.trckr.core.TrackerAdapter

internal interface TestTracker

internal class TestAdapter: TrackerAdapter {

    override fun trackEvent(eventName: String, parameters: Map<String, String>) = Unit
}

internal class TestConverter: GenericParamConverter() {

    override fun convert(value: Any?) = super.convert(value)
}