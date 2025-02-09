package ru.cookedapp.trckr.processor

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Tracker
import ru.cookedapp.trckr.processor.testHelpers.addImport
import ru.cookedapp.trckr.processor.testHelpers.finishedWith
import ru.cookedapp.trckr.processor.testHelpers.ktSourceFile

class TrckrProcessorTrackerGenerationTest : BaseKspTest() {

    @Test
    fun `should generate tracker when no errors in code`() {
        val result = testTrackerGeneration(
            """
                @Tracker
                interface Test {
                
                    @Event
                    fun event()
                }
            """,
        ) { result ->
            result finishedWith KotlinCompilation.ExitCode.OK
            result.classLoader.loadClass("$PACKAGE.TestImpl")
        }
    }

    private fun testTrackerGeneration(
        @Language("kotlin") code: String,
        assertCompilationResult: (JvmCompilationResult) -> Unit,
    ) = compileFiles(
        kspProcessorProvider = TrckrProcessorProvider(),
        ktSourceFile(
            buildString {
                append("package $PACKAGE;")
                addImport<Tracker>()
                addImport<Event>()
                append(code.trimIndent())
            },
        ),
        assertCompilationResult = assertCompilationResult,
    )

    companion object {

        private const val PACKAGE = "test"
    }
}
