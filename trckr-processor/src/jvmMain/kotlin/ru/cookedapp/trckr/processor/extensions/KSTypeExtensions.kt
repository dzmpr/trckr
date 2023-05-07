package ru.cookedapp.trckr.processor.extensions

import com.google.devtools.ksp.symbol.KSType

internal fun KSType.getSimpleName(): String = declaration.simpleName.asString()
