package ru.cookedapp.trckr.processor.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.withIndent
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.data.TrackStrategy
import ru.cookedapp.trckr.core.event.TrckrEvent
import ru.cookedapp.trckr.core.param.TrckrParam
import ru.cookedapp.trckr.processor.extensions.getAnnotation
import ru.cookedapp.trckr.processor.extensions.getArgumentWithName
import ru.cookedapp.trckr.processor.extensions.name
import ru.cookedapp.trckr.processor.helpers.Imports
import ru.cookedapp.trckr.processor.helpers.addCodeBlock
import ru.cookedapp.trckr.processor.helpers.addParameter
import ru.cookedapp.trckr.processor.helpers.createFunction

internal class EventGenerator(
    private val isKsp2: Boolean,
) {

    fun generateEvent(
        method: KSFunctionDeclaration,
        trackerCoreProperty: PropertySpec,
    ) = createFunction(functionName = method.simpleName.asString()) {
        addModifiers(KModifier.OVERRIDE)
        method.parameters.forEach(::addParameter)

        val annotation = method.getAnnotation<Event>()
        val annotatedName = annotation.getArgumentWithName<String>(Event.NAME_PROPERTY_NAME)
        val skippedAdapters = annotation.getArgumentWithName<ArrayList<KSType>>(Event.SKIP_ADAPTERS_PROPERTY_NAME)
        addCodeBlock {
            addStatement("val event = %T(", TrckrEvent::class)
            withIndent {
                addEventName(annotatedName, method)
                addSkippedAdapters(skippedAdapters)
                addEventParameters(method.parameters)
            }
            addStatement(")")
        }
        addStatement("%N.track(event)", trackerCoreProperty)
    }

    private fun CodeBlock.Builder.addEventName(
        annotatedName: String,
        eventMethod: KSFunctionDeclaration,
    ) {
        val eventName = annotatedName.takeIf(String::isNotBlank)
        val methodName = eventMethod.simpleName.asString()
        addStatement("name = %S,", eventName ?: methodName)
    }

    private fun CodeBlock.Builder.addSkippedAdapters(adapters: ArrayList<KSType>) {
        if (adapters.isEmpty()) {
            addStatement("skipAdapters = %M(),", Imports.emptyList)
        } else {
            addStatement("skipAdapters = %M(", Imports.listOf)
            withIndent {
                adapters.forEach { adapter ->
                    addStatement("%T::class,", adapter.toClassName())
                }
            }
            addStatement("),")
        }
    }

    private fun CodeBlock.Builder.addEventParameters(
        parameters: List<KSValueParameter>,
    ) {
        if (parameters.isEmpty()) {
            addStatement("parameters = %M(),", Imports.emptyList)
        } else {
            addStatement("parameters = %M(", Imports.listOf)
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
        val trackStrategyTypeClassName = if (isKsp2) {
            annotation.getArgumentWithName<KSClassDeclaration>(Param.STRATEGY_PROPERTY_NAME).toClassName()
        } else {
            annotation.getArgumentWithName<KSType>(Param.STRATEGY_PROPERTY_NAME).toClassName()
        }
        // TODO: Is there a better way to get enum entry name?
        val trackStrategy = trackStrategyTypeClassName.simpleName
        val parameterStatement = "%T(name = %S, strategy = %T.${trackStrategy}, value = ${parameter.name()}),"
        addStatement(parameterStatement, TrckrParam::class, parameterTrackName, TrackStrategy::class)
    }
}
