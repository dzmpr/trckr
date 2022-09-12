package ru.cooked.trckr.adapters

import ru.cooked.trckr.core.adapter.TrackerAdapter

class SimpleAdapter(private val adapterName: String) : TrackerAdapter {

    override fun trackEvent(eventName: String, parameters: Map<String, String>) {
        if (parameters.isEmpty()) {
            println("[$adapterName] Event: \"$eventName\".")
        } else {
            println("[$adapterName] Event: \"$eventName\". Params: \"$parameters\".")
        }
    }
}