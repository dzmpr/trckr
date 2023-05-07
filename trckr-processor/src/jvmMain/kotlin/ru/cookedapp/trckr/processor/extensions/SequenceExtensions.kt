package ru.cookedapp.trckr.processor.extensions

internal fun <T : Any> Sequence<T>.isEmpty(): Boolean = !iterator().hasNext()
