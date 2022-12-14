package ru.cookedapp.trckr.demo

import ru.cookedapp.trckr.demo.adapters.AdjustTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cookedapp.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cookedapp.trckr.demo.converters.EnumConverter
import ru.cookedapp.trckr.demo.converters.StopAppConverter
import ru.cookedapp.trckr.demo.tracker.ApplicationTracker
import ru.cookedapp.trckr.demo.tracker.createApplicationTracker
import ru.cookedapp.trckr.demo.tracker.data.SearchSource

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
        searchOpened(
            query = null,
            source = SearchSource.DETAILS_SCREEN,
        )
        searchOpened(
            query = "query",
            source = SearchSource.DETAILS_SCREEN,
        )
        appStopped(isForceStopped = true)
    }
}
