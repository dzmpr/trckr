package ru.cooked.trckr

import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.converter.GenericParamConverter
import ru.cooked.trckr.core.adapter.TrackerAdapter

internal interface TestTracker

internal interface IncompleteEventTestTracker {

    @Event
    fun methodWithAnnotation()

    fun methodWithoutAnnotation()
}

internal interface IncompleteParamTestTracker {

    @Event
    fun method(
        @Param(name = "id") id: Int,
        parameterWithoutAnnotation: String,
    )
}

internal interface TestTrackerWithNonUnitReturnType {

    @Event
    fun methodWithoutReturn()

    @Event
    fun methodWithReturn(): Int
}

internal class TestAdapter: TrackerAdapter {

    override fun trackEvent(eventName: String, parameters: Map<String, String>) = Unit
}

internal class TestConverter: GenericParamConverter() {

    override fun convert(value: Any?) = super.convert(value)
}