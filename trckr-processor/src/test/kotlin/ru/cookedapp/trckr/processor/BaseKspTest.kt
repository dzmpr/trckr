package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders

abstract class BaseKspTest {

    protected fun compileFiles(
        kspProcessorProvider: SymbolProcessorProvider,
        vararg files: SourceFile,
    ) = KotlinCompilation().apply {
        sources = files.toList()
        symbolProcessorProviders = listOf(kspProcessorProvider)
        inheritClassPath = true
    }.compile()
}
