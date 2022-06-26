package ru.cooked.trckr.tracker

import ru.cooked.trckr.adapters.AdjustTrackingAdapter
import ru.cooked.trckr.adapters.AmplitudeTrackingAdapter
import ru.cooked.trckr.adapters.FirebaseTrackingAdapter
import ru.cooked.trckr.annotations.Event
import ru.cooked.trckr.annotations.Param
import ru.cooked.trckr.annotations.SkipAdapters
import ru.cooked.trckr.annotations.SkipIfNull
import ru.cooked.trckr.tracker.data.SearchSource

interface ApplicationTracker {

    @Event("User open app")
    @SkipAdapters(AdjustTrackingAdapter::class)
    fun appLaunched()

    @Event("Stop app")
    @SkipAdapters(AmplitudeTrackingAdapter::class)
    fun appStopped(@Param("App stopped") isForceStopped: Boolean)

    @Event
    @SkipAdapters(
        FirebaseTrackingAdapter::class,
        AmplitudeTrackingAdapter::class,
    )
    fun searchOpened(
        @Param("query") @SkipIfNull query: String?,
        @Param("Search opened from") source: SearchSource,
    )
}