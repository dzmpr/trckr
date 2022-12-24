package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import kotlin.test.assertEquals
import ru.cookedapp.trckr.processor.testHelpers.kspGeneratedSourceFiles
import ru.cookedapp.trckr.processor.testHelpers.kspGeneratedSources

abstract class BaseKspTest {

    protected fun compileFilesWithGeneratedSources(
        kspProcessorProvider: SymbolProcessorProvider,
        vararg sourceFiles: SourceFile,
    ): KotlinCompilation.Result {
        println("Generate KSP sources.")
        val generateKspSourcesCompilation = KotlinCompilation().apply {
            sources = sourceFiles.toList()
            symbolProcessorProviders = listOf(kspProcessorProvider)
            inheritClassPath = true
            messageOutputStream = System.out
        }
        val generateSourcesResult = generateKspSourcesCompilation.compile()
        assertEquals(
            KotlinCompilation.ExitCode.OK,
            generateSourcesResult.exitCode,
            message = "Generate KSP sources failed.",
        )
        println("Compile KSP sources.")
        val kspGeneratedSourceFiles = generateSourcesResult.kspGeneratedSources().kspGeneratedSourceFiles()
        val compileKspSourcesCompilation = KotlinCompilation().apply {
            sources = kspGeneratedSourceFiles + generateKspSourcesCompilation.sources
            inheritClassPath = true
            messageOutputStream = System.out
        }
        val compileSourcesResult = compileKspSourcesCompilation.compile()
        assertEquals(
            KotlinCompilation.ExitCode.OK,
            compileSourcesResult.exitCode,
            message = "Compile KSP sources failed.",
        )
        return compileSourcesResult
    }

    protected fun compileFiles(
        kspProcessorProvider: SymbolProcessorProvider,
        vararg files: SourceFile,
    ) = KotlinCompilation().apply {
        sources = files.toList()
        symbolProcessorProviders = listOf(kspProcessorProvider)
        inheritClassPath = true
        messageOutputStream = System.out
    }.compile()
}
