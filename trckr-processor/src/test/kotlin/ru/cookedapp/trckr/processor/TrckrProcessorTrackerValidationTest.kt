package ru.cookedapp.trckr.processor

import com.tschuchort.compiletesting.KotlinCompilation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.Tracker
import ru.cookedapp.trckr.processor.testHelpers.addImport
import ru.cookedapp.trckr.processor.testHelpers.finishedWith
import ru.cookedapp.trckr.processor.testHelpers.hasMessage
import ru.cookedapp.trckr.processor.testHelpers.hasNoKspErrors
import ru.cookedapp.trckr.processor.testHelpers.ktSourceFile

class TrckrProcessorTrackerValidationTest : BaseKspTest() {

    private val defaultTrackerName = "TestTracker"
    private val defaultMethodName = "testEvent"
    private val defaultParameterName = "testParameter"

    @Test
    fun `should not fail when interface annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker interface TestTracker",
        expectedCode = KotlinCompilation.ExitCode.OK,
    )

    @Test
    fun `should fail when abstract class annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker abstract class TestTracker",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should fail when class annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker class TestTracker",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should fail when data class annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker data class TestTracker(val data: Int)",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should fail when enum class annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker enum class TestTracker",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should fail when annotation class annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker annotation class TestTracker",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should fail when object annotated with @Tracker annotation`() = testTrckrProcessor(
        code = "@Tracker object TestTracker",
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET,
    )

    @Test
    fun `should not fail when tracker contains companion object declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                companion object
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.OK,
    )

    @Test
    fun `should fail when tracker contains class declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                class InnerClass
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_CLASS_DECLARATION,
    )

    @Test
    fun `should fail when tracker contains interface declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                interface InnerInterface
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_CLASS_DECLARATION,
    )

    @Test
    fun `should fail when tracker contains object declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                object InnerObject
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_CLASS_DECLARATION,
    )

    @Test
    fun `should fail when tracker contains data class declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                data class InnerDataClass(val data: Int)
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.INCORRECT_TRACKER_CLASS_DECLARATION,
    )

    @Test
    fun `should fail when tracker contains typealias declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface $defaultTrackerName {
                
                typealias IntAlias = Int
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.incorrectTrackerInnerDeclaration(defaultTrackerName),
    )

    @Test
    fun `should fail when tracker contains property declaration`() = testTrckrProcessor(
        code = """
            @Tracker
            interface $defaultTrackerName {
                
                val property: Int
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.incorrectTrackerInnerDeclaration(defaultTrackerName),
    )

    @Test
    fun `should not fail when event method has @Event annotation, not suspendable, has Unit return type and has no parameters`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                @Event
                fun testEvent()
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.OK,
    )

    @Test
    fun `should not fail when event method has @Event annotation, not suspendable, has Unit return type and has correct parameters`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                @Event
                fun testEvent(@Param(name = "data") data: Int)
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.OK,
    )

    @Test
    fun `should fail when event method has no @Event annotation`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {

                fun $defaultMethodName()
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.eventMethodMissingAnnotation(defaultMethodName),
    )

    @Test
    fun `should fail when event method is suspendable`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                @Event
                suspend fun $defaultMethodName()
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.suspendableEventMethod(defaultMethodName),
    )

    @Test
    fun `should fail when event method return type is not Unit`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                @Event
                fun $defaultMethodName(): Int
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.incorrectEventMethodReturnType(defaultMethodName),
    )

    @Test
    fun `should fail when event method has parameter without @Param annotation`() = testTrckrProcessor(
        code = """
            @Tracker
            interface TestTracker {
                
                @Event
                fun $defaultMethodName($defaultParameterName: Int)
            }
        """,
        expectedCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        expectedMessage = ErrorMessage.incorrectParameterDeclaration(defaultMethodName, defaultParameterName),
    )

    private fun testTrckrProcessor(
        @Language("kotlin") code: String,
        expectedCode: KotlinCompilation.ExitCode,
        expectedMessage: String? = null,
    ) {
        val file = ktSourceFile(
            buildString {
                append("package test;")
                addImport<Tracker>()
                addImport<Event>()
                addImport<Param>()
                append(code.trimIndent())
            }
        )
        val result = compileFiles(kspProcessorProvider = TrckrProcessorProvider(), file)
        result finishedWith expectedCode
        if (expectedMessage != null) {
            result hasMessage expectedMessage
        } else {
            result.hasNoKspErrors()
        }
    }
}
