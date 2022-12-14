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
    private val options: Map<String, String>,
    codeGenerator: CodeGenerator,
    override val logger: KSPLogger,
) : SymbolProcessor, LoggerOwner {

    private val generator = TrackerGenerator(codeGenerator)

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
            logError("@Tracker annotation can be applied only to interface.")
            false
        }
        !isInterface() -> {
            logError("@Tracker annotation can be applied only to interface.")
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
                    logError("Tracker can't contain classes (only companion objects allowed).'")
                    false
                } else {
                    true
                }
            }
            else -> {
                logError("Tracker \"${simpleName.asString()}\" can contain only event methods or companion object.")
                false
            }
        }
    }

    private fun KSFunctionDeclaration.isFunctionValid(resolver: Resolver): Boolean {
        // Has @Event annotation
        if (!hasAnnotation(resolver.getTypeOf<Event>())) {
            logError("Event method \"${simpleName.asString()}\" should be annotated with @Event annotation.")
            return false
        }
        // Is not suspendable
        if (isSuspendable()) {
            logError("Event method \"${simpleName.asString()}\" should not be suspendable.")
            return false
        }
        // Return type is Unit
        if (returnType?.resolve() != resolver.builtIns.unitType) {
            logError("Event method \"${simpleName.asString()}\" should return Unit.")
            return false
        }
        // All event parameters has @Param annotation
        val paramType = resolver.getTypeOf<Param>()
        parameters.forEach { parameter ->
            if (parameter.hasAnnotation(paramType)) return@forEach
            logError("Event method \"${simpleName.asString()}\" has parameter \"${parameter.name()}\" without @Param annotation.")
            return false
        }
        return true
    }
}
