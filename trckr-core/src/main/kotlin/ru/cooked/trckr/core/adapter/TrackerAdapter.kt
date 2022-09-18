package ru.cooked.trckr.core.adapter

interface TrackerAdapter {

    fun trackEvent(eventName: String, parameters: Map<String, Any?>)
}
