package ru.cookedapp.trckr.core.annotations

import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.annotations.internal.TrckrInternal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
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
