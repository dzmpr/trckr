package ru.cooked.trckr.core.annotations

import kotlin.reflect.KClass
import ru.cooked.trckr.core.adapter.TrackerAdapter

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkipAdapters(vararg val adapters: KClass<out TrackerAdapter>)
