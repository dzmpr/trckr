package ru.cookedapp.trckr.core.annotations

import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.internal.TrckrInternal

/**
 * Annotation for [@Tracker][Tracker]-marked interface to provide additional event information.
 *
 * All methods of tracker should be annotated with this annotation. It has no required parameters.
 * When no string passed to [name] parameter **trckr** processor use name of this method as
 * event name.
 *
 * All adapters passed in [skipAdapters] array would be not used to track this event.
 *
 * Example event:
 * ```kotlin
 * // ...
 * @Event(
 *     name = "Example event name",
 *     skipAdapters = [ExampleAdapter::class],
 * )
 * fun exampleEvent()
 * // ...
 * ```
 *
 * @param name Name of the event, that be used to track this event.
 * @param skipAdapters Array of adapters, for which this event should not be tracked by **trckr**.
 *
 * @see Tracker
 * @see Param
 */
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
