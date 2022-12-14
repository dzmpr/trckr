package ru.cookedapp.trckr.processor.extensions

import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

internal fun KClass<*>.getQualifiedName(): String =
    qualifiedName ?: error("Class $this has no qualified name.")

internal val KClass<*>.packageName: String
    get() = asTypeName().packageName
