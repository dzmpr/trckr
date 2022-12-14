package ru.cookedapp.trckr.processor.generator

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.withIndent
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.processor.extensions.getAnnotation
import ru.cookedapp.trckr.processor.extensions.getArgumentWithName
import ru.cookedapp.trckr.processor.extensions.getSimpleName
import ru.cookedapp.trckr.processor.extensions.name
import ru.cookedapp.trckr.processor.helpers.addCodeBlock
import ru.cookedapp.trckr.processor.helpers.addParameter
import ru.cookedapp.trckr.processor.helpers.createFunction

internal class EventGenerator {

    fun generateEvent(
        method: KSFunctionDeclaration,
        eventProcessorPropertyName: String,
    ) = createFunction(method.simpleName.asString()) {
        addModifiers(KModifier.OVERRIDE)
        method.parameters.forEach(::addParameter)

        val annotation = method.getAnnotation<Event>()
        val annotatedName = annotation.getArgumentWithName<String>(Event.NAME_PROPERTY_NAME)
        val skippedAdapters = annotation.getArgumentWithName<ArrayList<KSType>>(Event.SKIP_ADAPTERS_PROPERTY_NAME)
        addCodeBlock {
            addStatement("val event = TrckrEvent(")
            withIndent {
                addEventName(annotatedName, method)
                addSkippedAdapters(skippedAdapters)
                addEventParameters(method.parameters)
            }
            addStatement(")")
        }
        addStatement("${eventProcessorPropertyName}.track(event)")
    }

    private fun CodeBlock.Builder.addEventName(
        annotatedName: String,
        eventMethod: KSFunctionDeclaration,
    ) {
        val eventName = annotatedName.takeIf { it.isNotBlank() }
        val methodName = eventMethod.simpleName.asString()
        addStatement("name = %S,", eventName ?: methodName)
    }

    private fun CodeBlock.Builder.addSkippedAdapters(adapters: ArrayList<KSType>) {
        if (adapters.isEmpty()) {
            addStatement("skipAdapters = emptyList(),")
        } else {
            addStatement("skipAdapters = listOf(")
            withIndent {
                adapters.forEach { adapter ->
                    addStatement("${adapter.getSimpleName()}::class,")
                }
            }
            addStatement("),")
        }
    }

    private fun CodeBlock.Builder.addEventParameters(
        parameters: List<KSValueParameter>,
    ) {
        if (parameters.isEmpty()) {
            addStatement("parameters = emptyList(),")
        } else {
            addStatement("parameters = listOf(")
            withIndent {
                parameters.forEach { parameter ->
                    addParameter(parameter)
                }
            }
            addStatement("),")
        }
    }


    private fun CodeBlock.Builder.addParameter(
        parameter: KSValueParameter,
    ) {
        val annotation = parameter.getAnnotation<Param>()
        val parameterTrackName = annotation.getArgumentWithName<String>(Param.NAME_PROPERTY_NAME)
        val trackStrategyType = annotation.getArgumentWithName<KSType>(Param.STRATEGY_PROPERTY_NAME)
        // TODO: Is there a better way to get enum entry name?
        val trackStrategy = trackStrategyType.toClassName().simpleName
        val parameterStatement = buildString {
            append("TrckrParam(")
            append("name = %S, ")
            append("strategy = TrackStrategy.$trackStrategy, ")
            append("value = ${parameter.name()}),")
        }
        addStatement(parameterStatement, parameterTrackName)
    }
}
