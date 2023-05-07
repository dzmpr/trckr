package ru.cookedapp.trckr.core.param

import ru.cookedapp.trckr.core.annotations.data.TrackStrategy

/**
 * Container for parameter data that created by generated tracked implementation.
 *
 * You don't need to create this class manually.
 *
 * @param name Name of parameter.
 * @param strategy Conversion strategy that should be used when converting value of this parameter.
 * @param value Argument that passed to event method.
 *
 * @see TrackStrategy
 */
data class TrckrParam(
    val name: String,
    val strategy: TrackStrategy,
    val value: Any?,
)
