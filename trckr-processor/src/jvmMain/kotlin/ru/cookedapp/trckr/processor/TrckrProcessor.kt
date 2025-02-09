package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.Tracker
import ru.cookedapp.trckr.processor.extensions.getAllDeclarations
import ru.cookedapp.trckr.processor.extensions.getSymbolsWithAnnotation
import ru.cookedapp.trckr.processor.extensions.getTypeOf
import ru.cookedapp.trckr.processor.extensions.hasAnnotation
import ru.cookedapp.trckr.processor.extensions.isEmpty
import ru.cookedapp.trckr.processor.extensions.isInterface
import ru.cookedapp.trckr.processor.extensions.isSuspendable
import ru.cookedapp.trckr.processor.extensions.name
import ru.cookedapp.trckr.processor.generator.TrackerGenerator
import ru.cookedapp.trckr.processor.helpers.LoggerOwner
import ru.cookedapp.trckr.processor.helpers.logError
import ru.cookedapp.trckr.processor.helpers.logInfo

class TrckrProcessor(
    override val logger: KSPLogger,
    private val isKsp2: Boolean,
    codeGenerator: CodeGenerator,
) : SymbolProcessor, LoggerOwner {

    private val generator = TrackerGenerator(
        codeGenerator = codeGenerator,
        isKsp2 = isKsp2,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logInfo("Start trckr processing round.")
        val resolvedSymbols = resolver.getSymbolsWithAnnotation<Tracker>()

        val invalidSymbols = if (!resolvedSymbols.isEmpty()) {
            val (validTrackers, invalidTrackers) = resolvedSymbols.partition { it.validate() }
            validTrackers.filter { symbol ->
                symbol.isSymbolValid(resolver)
            }.forEach { classDeclaration ->
                classDeclaration.accept(generator, data = Unit)
            }
            invalidTrackers
        } else {
            emptyList()
        }
        logInfo("Finish trckr processing round.")
        return invalidSymbols
    }

    private fun KSAnnotated.isSymbolValid(resolver: Resolver): Boolean = when {
        this !is KSClassDeclaration -> {
            logError(ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET)
            false
        }
        !isInterface() -> {
            logError(ErrorMessage.INCORRECT_TRACKER_ANNOTATION_TARGET)
            false
        }
        !isInterfaceValid(resolver) -> false
        else -> true
    }

    private fun KSClassDeclaration.isInterfaceValid(
        resolver: Resolver,
    ): Boolean = getAllDeclarations().all { declaration ->
        when (declaration) {
            is KSFunctionDeclaration -> declaration.isFunctionValid(resolver)
            is KSClassDeclaration -> {
                if (!declaration.isCompanionObject) {
                    logError(ErrorMessage.INCORRECT_TRACKER_CLASS_DECLARATION)
                    false
                } else {
                    true
                }
            }
            else -> {
                logError(ErrorMessage.incorrectTrackerInnerDeclaration(simpleName.asString()))
                false
            }
        }
    }

    private fun KSFunctionDeclaration.isFunctionValid(resolver: Resolver): Boolean {
        // Has @Event annotation
        if (!hasAnnotation(resolver.getTypeOf<Event>())) {
            logError(ErrorMessage.eventMethodMissingAnnotation(simpleName.asString()))
            return false
        }
        // Is not suspendable
        if (isSuspendable()) {
            logError(ErrorMessage.suspendableEventMethod(simpleName.asString()))
            return false
        }
        // Return type is Unit
        if (returnType?.resolve() != resolver.builtIns.unitType) {
            logError(ErrorMessage.incorrectEventMethodReturnType(simpleName.asString()))
            return false
        }
        // All event parameters has @Param annotation
        val paramType = resolver.getTypeOf<Param>()
        parameters.forEach { parameter ->
            if (parameter.hasAnnotation(paramType)) return@forEach
            val errorMessage = ErrorMessage.incorrectParameterDeclaration(
                methodName = simpleName.asString(),
                parameterName = parameter.name(),
            )
            logError(errorMessage)
            return false
        }
        return true
    }
}
