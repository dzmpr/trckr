package ru.cookedapp.trckr.demo.tracker

import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.Tracker
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cookedapp.trckr.demo.tracker.data.ScreenType

@Tracker
interface ApplicationTracker : LifecycleTracker, ProductTracker

interface LifecycleTracker {

    @Event("User open app")
    fun appLaunched()

    @Event(
        name = EVENT_STOP_APP,
        skipAdapters = [AmplitudeTrackingAdapter::class],
    )
    fun appStopped(@Param(PARAM_APP_STOPPED) isForceStopped: Boolean)

    companion object {
        const val EVENT_STOP_APP = "Stop app"
        const val PARAM_APP_STOPPED = "App stopped"
    }
}


interface ProductTracker {

    @Event(skipAdapters = [FirebaseTrackingAdapter::class])
    fun screenOpened(
        @Param(
            name = "Utm data",
            strategy = TrackStrategy.SKIP_IF_NULL,
        ) utmData: String? = null,
        @Param("Screen name") screen: ScreenType,
    )
}
