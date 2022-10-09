package ru.cookedapp.trckr.demo.converters

import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.demo.tracker.ApplicationTracker.Companion.EVENT_STOP_APP
import ru.cookedapp.trckr.demo.tracker.ApplicationTracker.Companion.PARAM_APP_STOPPED

class StopAppConverter : ParameterConverter {

    override fun convert(eventName: String, parameterName: String, value: Any?): String? {
        if (eventName != EVENT_STOP_APP && parameterName != PARAM_APP_STOPPED) return null
        val isAppForceStopped = value as Boolean
        return if (isAppForceStopped) "Force stopped" else "Stopped normal"
    }
}
