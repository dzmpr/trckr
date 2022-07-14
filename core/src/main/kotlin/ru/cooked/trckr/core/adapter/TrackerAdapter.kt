package ru.cooked.trckr.core.adapter

fun interface TrackerAdapter {

    fun trackEvent(eventName: String, parameters: Map<String, String>)
}
