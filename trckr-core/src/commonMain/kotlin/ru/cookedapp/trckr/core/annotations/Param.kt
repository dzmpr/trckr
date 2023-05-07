package ru.cookedapp.trckr.core.annotations

import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.annotations.internal.TrckrInternal

/**
 * Annotation for parameter of event method.
 *
 * This annotation provides parameter name and specify behaviour for parameter value conversion.
 *
 * Example usage:
 * ```kotlin
 * // ...
 * @Event
 * fun exampleEvent(
 *     @Param(
 *         name = "Parameter name",
 *         strategy = TrackStrategy.SKIP_IF_NULL,
 *     ) count: Int?,
 * )
 * // ...
 * ```
 *
 * @param name Name of parameter that would be tracked. Required parameter.
 * @param strategy Specify value conversion behavior. By default, it is set to
 * [TrackStrategy.DEFAULT]. More about strategies: [TrackStrategy].
 *
 * @see Event
 * @see TrackStrategy
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
annotation class Param(
    val name: String,
    val strategy: TrackStrategy = TrackStrategy.DEFAULT,
) {

    companion object {

        @TrckrInternal
        const val NAME_PROPERTY_NAME = "name"

        @TrckrInternal
        const val STRATEGY_PROPERTY_NAME = "strategy"
    }
}
