package ru.cookedapp.trckr.processor.testHelpers

import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal infix fun KotlinCompilation.Result.finishedWith(expectedCode: KotlinCompilation.ExitCode) {
    assertEquals(expectedCode, exitCode)
}

internal infix fun KotlinCompilation.Result.hasMessage(expectedMessage: String) {
    assertTrue(messages.contains(expectedMessage))
}

internal fun KotlinCompilation.Result.hasNoKspErrors() {
    assertFalse(messages.contains("e: Error occurred in KSP, check log for detail"))
}

internal fun KotlinCompilation.Result.kspGeneratedSources() =
    outputDirectory.parentFile.resolve("ksp/sources").walk().filter { it.isFile }.toList()
