package ru.cookedapp.trckr.processor.testHelpers

import com.tschuchort.compiletesting.SourceFile
import java.io.File
import org.intellij.lang.annotations.Language

internal fun ktSourceFile(@Language("kotlin") code: String, name: String = "Test") =
    SourceFile.kotlin(name = "$name.kt", contents = code, trimIndent = false)

internal fun List<File>.kspGeneratedSourceFiles() = map { SourceFile.fromPath(it.absoluteFile) }
