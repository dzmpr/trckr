package ru.cookedapp.trckr.demo

import ru.cookedapp.trckr.demo.adapters.AdjustTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cookedapp.trckr.demo.converters.EnumConverter
import ru.cookedapp.trckr.demo.converters.StopAppConverter
import ru.cookedapp.trckr.demo.tracker.ApplicationTracker
import ru.cookedapp.trckr.demo.tracker.createApplicationTracker
import ru.cookedapp.trckr.demo.tracker.data.ScreenType

fun getTracker(): ApplicationTracker = createApplicationTracker {
    addAdapter(FirebaseTrackingAdapter())
    addAdapter(AmplitudeTrackingAdapter())
    addAdapter(AdjustTrackingAdapter())
    addTypeConverter(EnumConverter())
    addParameterConverter(StopAppConverter())
}

fun main() {
    with(getTracker()) {
        appLaunched()
        screenOpened(screen = ScreenType.MAIN)
        screenOpened(
            utmData = "utmCampaign=release",
            screen = ScreenType.DETAILS,
        )
        appStopped(isForceStopped = true)
    }
}
