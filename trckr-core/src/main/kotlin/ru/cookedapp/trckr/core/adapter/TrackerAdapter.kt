package ru.cookedapp.trckr.core.adapter

interface TrackerAdapter {

    fun trackEvent(eventName: String, parameters: Map<String, Any?>)
}
