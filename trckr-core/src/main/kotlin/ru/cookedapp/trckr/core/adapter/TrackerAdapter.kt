package ru.cookedapp.trckr.core.adapter

/**
 * Implementation of [this][TrackerAdapter] interface encapsulates all tracking logic to specific service.
 *
 * Adapter should be registered when you create a tracker by calling `createExampleTracker
 *  { addAdapter(ExampleAdapterImpl()) }`
 *
 * Example adapter:
 * ```kotlin
 * class FirebaseAdapter : TrackerAdapter {
 *
 *     private val firebaseTracker = Firebase.analytics
 *
 *     override fun trackEvent(eventName: String, parameters: Map<String, Any?>) {
 *         firebaseTracker.logEvent(eventName, parameters.toBundle())
 *     }
 * }
 * ```
 */
interface TrackerAdapter {

    /**
     * Trckr invoke this method to track event.
     *
     * It can be not called when type of this adapter is listed in `skipAdapters` parameter of
     * [@Event][ru.cookedapp.trckr.core.annotations.Event] annotation.
     *
     * @param eventName Name of the event. It can be either [name][ru.cookedapp.trckr.core.annotations.Event.name]
     * parameter of [@Event][ru.cookedapp.trckr.core.annotations.Event] annotation or method name of
     * event if [name][ru.cookedapp.trckr.core.annotations.Event.name] is empty.
     * @param parameters Map of converted parameters to track. Keys are names specified by [name][ru.cookedapp.trckr.core.annotations.Param]
     * parameter of [@Param][ru.cookedapp.trckr.core.annotations.Param] annotation. Values are 'values'
     * that was successfully passed conversion phase.
     */
    fun trackEvent(eventName: String, parameters: Map<String, Any?>)
}
