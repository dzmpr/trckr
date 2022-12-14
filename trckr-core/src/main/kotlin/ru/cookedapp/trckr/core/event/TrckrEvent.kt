package ru.cookedapp.trckr.core.event

import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.param.TrckrParam

data class TrckrEvent(
    val name: String,
    val skipAdapters: List<KClass<out TrackerAdapter>>,
    val parameters: List<TrckrParam>,
)
