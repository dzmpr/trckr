package ru.cookedapp.trckr.core.annotations

import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.internal.TrckrInternal

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Event(
    val name: String = "",
    val skipAdapters: Array<KClass<out TrackerAdapter>> = [],
) {

    companion object {

        @TrckrInternal
        const val NAME_PROPERTY_NAME = "name"

        @TrckrInternal
        const val SKIP_ADAPTERS_PROPERTY_NAME = "skipAdapters"
    }
}
