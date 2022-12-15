package ru.cookedapp.trckr.core

import ru.cookedapp.trckr.core.event.TrckrEvent

/**
 * [TrckrCore] implementation is used by generated tracker class to track
 * assembled events.
 */
interface TrckrCore {

    /**
     * This method called by generated tracker to track event.
     *
     * @param event [TrckrEvent] that created by generated tracker.
     * @see TrckrEvent
     */
    fun track(event: TrckrEvent)
}
