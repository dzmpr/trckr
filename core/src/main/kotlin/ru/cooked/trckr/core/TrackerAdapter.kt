package ru.cooked.trckr.core

fun interface TrackerAdapter {

    fun trackEvent(eventName: String, parameters: Map<String, String>)
}
