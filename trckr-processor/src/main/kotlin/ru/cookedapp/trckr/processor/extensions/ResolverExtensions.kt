package ru.cookedapp.trckr.processor.extensions

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType

internal inline fun <reified T : Any> Resolver.getSymbolsWithAnnotation(): Sequence<KSAnnotated> =
    getSymbolsWithAnnotation(T::class.getQualifiedName())

internal inline fun <reified T : Any> Resolver.getTypeOf(): KSType =
    getClassDeclarationByName(T::class.getQualifiedName())?.asType(emptyList())
        ?: error("Can't get KSType for \"${T::class}\".")
