package ru.cooked.trckr

import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.annotations.SkipIfNull
import ru.cooked.trckr.core.annotations.TrackNull
import ru.cooked.trckr.core.converter.ParameterConverter
import ru.cooked.trckr.core.converter.TypeConverter

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

internal interface TestTrackerWithIncompatibleParamAnnotations {
    @Event
    fun method(
        @TrackNull
        @SkipIfNull
        @Param(name = "name")
        parameter: Int?,
    )
}

internal class TestAdapter : TrackerAdapter {

    override fun trackEvent(eventName: String, parameters: Map<String, Any?>) = Unit
}

internal class TestTypeConverter : TypeConverter {

    override fun convert(value: Any?) = null
}

internal class TestParameterConverter : ParameterConverter {

    override fun convert(eventName: String, parameterName: String, value: Any?): String? = null
}
