package ru.cookedapp.trckr.demo.adapters

import ru.cookedapp.trckr.core.adapter.TrackerAdapter

class SimpleAdapter(private val adapterName: String) : TrackerAdapter {

    override fun trackEvent(eventName: String, parameters: Map<String, Any?>) {
        if (parameters.isEmpty()) {
            println("[$adapterName] Event: \"$eventName\".")
        } else {
            println("[$adapterName] Event: \"$eventName\". Params: \"$parameters\".")
        }
    }
}
