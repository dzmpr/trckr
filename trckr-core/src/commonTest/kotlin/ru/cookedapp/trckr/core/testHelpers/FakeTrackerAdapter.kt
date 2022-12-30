package ru.cookedapp.trckr.core.testHelpers

import ru.cookedapp.trckr.core.adapter.TrackerAdapter

class FakeTrackerAdapter : TrackerAdapter {

    private val events: MutableList<Pair<String, Map<String, Any?>>> = mutableListOf()

    fun getEvents(): List<Pair<String, Map<String, Any?>>> = events

    override fun trackEvent(eventName: String, parameters: Map<String, Any?>) {
        events.add(eventName to parameters)
    }
}

fun createFakeAdapter() = FakeTrackerAdapter()
