package ru.cookedapp.trckr.demo.tracker

import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.Tracker
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cookedapp.trckr.demo.tracker.data.SearchSource

@Tracker
interface ApplicationTracker {

    @Event("User open app")
    fun appLaunched()

    @Event(
        name = EVENT_STOP_APP,
        [AmplitudeTrackingAdapter::class,]
    )
    fun appStopped(@Param(PARAM_APP_STOPPED) isForceStopped: Boolean)

    @Event(skipAdapters = [FirebaseTrackingAdapter::class, AmplitudeTrackingAdapter::class])
    fun searchOpened(
        @Param(
            name = "query",
            strategy = TrackStrategy.SKIP_IF_NULL,
        ) query: String?,
        @Param("Search opened from") source: SearchSource,
    )

    companion object {
        const val EVENT_STOP_APP = "Stop app"
        const val PARAM_APP_STOPPED = "App stopped"
    }
}
