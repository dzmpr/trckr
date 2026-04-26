package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp

abstract class BaseKspTest {

    protected fun compileFiles(
        kspProcessorProvider: SymbolProcessorProvider,
        vararg files: SourceFile,
        assertCompilationResult: (JvmCompilationResult) -> Unit,
    ) {
        val compilationResult = KotlinCompilation().apply {
            sources = files.toList()
            inheritClassPath = true
            messageOutputStream = System.out
            configureKsp {
                incremental = true
                withCompilation = true
                symbolProcessorProviders += listOf(kspProcessorProvider)
            }
        }.compile()

        assertCompilationResult(compilationResult)
    }
}
