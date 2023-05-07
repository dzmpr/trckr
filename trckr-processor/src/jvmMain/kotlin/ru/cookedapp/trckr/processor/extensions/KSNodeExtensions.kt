package ru.cookedapp.trckr.processor.extensions

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.google.devtools.ksp.symbol.KSModifierListOwner
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSClassDeclaration.isInterface(): Boolean = classKind == ClassKind.INTERFACE

internal fun KSClassDeclaration.toTypeName(
    typeArguments: List<KSTypeArgument> = emptyList(),
): TypeName = asType(typeArguments).toTypeName()

internal fun KSClassDeclaration.getAllDeclarations(): Sequence<KSDeclaration> {
    val superTypeDeclarations = getAllSuperTypes().filter { ksType ->
        !ksType.isAnyType()
    }.flatMap { ksType ->
        (ksType.declaration as KSDeclarationContainer).declarations
    }
    return declarations + superTypeDeclarations
}

internal inline fun <reified T : Any> KSAnnotated.getAnnotation(): KSAnnotation {
    val typeName = T::class.asTypeName()
    return annotations.find { ksAnnotation ->
        ksAnnotation.annotationType.toTypeName() == typeName
    } ?: error("Annotation with type \"${T::class}\" not found.")
}

internal fun KSAnnotated.hasAnnotation(type: KSType): Boolean {
    return annotations.any { annotation ->
        annotation.annotationType.resolve() == type
    }
}

internal fun KSAnnotation.getArgumentWithName(name: String): KSValueArgument {
    return arguments.firstOrNull { argument ->
        argument.name?.asString() == name
    } ?: error("Value argument with name \"$name\" not found.")
}

internal inline fun <reified T : Any> KSAnnotation.getArgumentWithName(name: String): T =
    getArgumentWithName(name).value as T

internal fun KSValueParameter.name(): String = name?.asString() ?: error("Parameter has no name.")

internal fun KSModifierListOwner.isSuspendable(): Boolean = Modifier.SUSPEND in modifiers

internal fun KSType.isAnyType(): Boolean = declaration.qualifiedName?.asString() == "kotlin.Any"
