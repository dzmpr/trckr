package ru.cookedapp.trckr.processor.testHelpers

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal infix fun JvmCompilationResult.finishedWith(expectedCode: KotlinCompilation.ExitCode) {
    assertEquals(expectedCode, exitCode)
}

internal infix fun JvmCompilationResult.hasMessage(expectedMessage: String) {
    assertTrue(messages.contains(expectedMessage))
}

internal fun JvmCompilationResult.hasNoKspErrors() {
    assertFalse(messages.contains("e: Error occurred in KSP, check log for detail"))
}
