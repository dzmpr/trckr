package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.event.TrckrEvent

interface TrckrCore {

    fun track(event: TrckrEvent)
}
