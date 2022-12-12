package ru.cookedapp.trckr.core.param

import ru.cookedapp.trckr.core.annotations.data.TrackStrategy

data class TrckrParam(
    val name: String,
    val strategy: TrackStrategy,
    val value: Any?,
)
