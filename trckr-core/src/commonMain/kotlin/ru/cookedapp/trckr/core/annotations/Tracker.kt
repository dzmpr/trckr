package ru.cookedapp.trckr.core.annotations

/**
 * [@Tracker][Tracker] is an **entry point** annotation for the **trckr** symbol processor.
 *
 * An annotation class that triggers generation of tracker implementation for annotated interface.
 * Methods of this interface considered as an events. **Trckr** generate implementation of this
 * interface that will forward all method calls to
 * [TrackerAdapter's][ru.cookedapp.trckr.core.adapter.TrackerAdapter], registered for this tracker.
 *
 * All interface methods should have [@Event][Event] annotation. Methods should be not suspendable,
 * return [Unit] and all method parameters should be annotated with [@Param][Param] annotation.
 * It is allowed to define companion object inside tracker interface. Nor regular class nor object
 * is not allowed.
 *
 * Example usage:
 * ```kotlin
 * @Tracker
 * interface ApplicationTracker {
 *
 *     @Event(name = "application started")
 *     fun launch()
 * }
 * ```
 *
 * @see [Event]
 * @see [Param]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Tracker
