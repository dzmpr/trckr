package ru.cooked.trckr.demo

import ru.cooked.trckr.core.Trckr
import ru.cooked.trckr.demo.adapters.AdjustTrackingAdapter
import ru.cooked.trckr.demo.adapters.AmplitudeTrackingAdapter
import ru.cooked.trckr.demo.adapters.FirebaseTrackingAdapter
import ru.cooked.trckr.demo.converters.EnumConverter
import ru.cooked.trckr.demo.converters.StopAppConverter
import ru.cooked.trckr.demo.tracker.ApplicationTracker
import ru.cooked.trckr.demo.tracker.data.SearchSource

fun getTracker(): ApplicationTracker = Trckr {
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
