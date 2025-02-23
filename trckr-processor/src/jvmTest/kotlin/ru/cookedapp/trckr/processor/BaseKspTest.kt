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
        val ksp1Result = compileFilesInternal(
            kspProcessorProvider = kspProcessorProvider,
            useKsp2 = false,
            files = files,
        )
        assertCompilationResult(ksp1Result)
        val ksp2Result = compileFilesInternal(
            kspProcessorProvider = kspProcessorProvider,
            useKsp2 = true,
            files = files,
        )
        assertCompilationResult(ksp2Result)
    }

    private fun compileFilesInternal(
        kspProcessorProvider: SymbolProcessorProvider,
        useKsp2: Boolean,
        vararg files: SourceFile,
    ) = KotlinCompilation().apply {
        sources = files.toList()
        inheritClassPath = true
        languageVersion = if (useKsp2) null else "1.9"
        messageOutputStream = System.out
        configureKsp(useKsp2) {
            incremental = true
            withCompilation = true
            symbolProcessorProviders += listOf(kspProcessorProvider)
        }
    }.compile()
}
