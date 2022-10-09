package ru.cooked.trckr.demo.tracker

import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.annotations.SkipAdapters
import ru.cooked.trckr.core.annotations.SkipIfNull
import ru.cooked.trckr.demo.adapters.AdjustTrackingAdapter
import ru.cooked.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cooked.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cooked.trckr.demo.tracker.data.SearchSource

interface ApplicationTracker {

    @Event("User open app")
    @SkipAdapters(AdjustTrackingAdapter::class)
    fun appLaunched()

    @Event(EVENT_STOP_APP)
    @SkipAdapters(AmplitudeTrackingAdapter::class)
    fun appStopped(@Param(PARAM_APP_STOPPED) isForceStopped: Boolean)

    @Event
    @SkipAdapters(
        FirebaseTrackingAdapter::class,
        AmplitudeTrackingAdapter::class,
    )
    fun searchOpened(
        @Param("query") @SkipIfNull query: String?,
        @Param("Search opened from") source: SearchSource,
    )

    companion object {
        const val EVENT_STOP_APP = "Stop app"
        const val PARAM_APP_STOPPED = "App stopped"
    }
}
