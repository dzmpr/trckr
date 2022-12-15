package ru.cookedapp.trckr.core.event

import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.param.TrckrParam

/**
 * Container for event data that created by generated tracked implementation.
 *
 * You don't need to create this class manually.
 *
 * @param name Name of event.
 * @param skipAdapters List of adapters which should not be used to track this event.
 * @param parameters List of [parameters][TrckrParam] that passed to event method.
 *
 * @see TrckrParam
 */
data class TrckrEvent(
    val name: String,
    val skipAdapters: List<KClass<out TrackerAdapter>>,
    val parameters: List<TrckrParam>,
)
