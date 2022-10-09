package ru.cookedapp.trckr.demo.tracker

import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.SkipAdapters
import ru.cookedapp.trckr.core.annotations.SkipIfNull
import ru.cookedapp.trckr.demo.adapters.AdjustTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cookedapp.trckr.demo.tracker.data.SearchSource

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
