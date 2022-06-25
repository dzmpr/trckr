package ru.cooked.trckr.annotations

import kotlin.reflect.KClass
import ru.cooked.trckr.core.TrackerAdapter

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkipAdapters(vararg val adapters: KClass<out TrackerAdapter>)
