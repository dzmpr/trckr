package ru.cookedapp.trckr.processor.helpers

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KClass

// FileSpec helpers

internal inline fun createFile(
    packageName: String,
    fileName: String,
    crossinline builder: FileSpec.Builder.() -> Unit,
): FileSpec = FileSpec.builder(packageName, fileName).apply(builder).build()

internal inline fun FileSpec.Builder.addClass(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(createClass(className, builder))

internal inline fun FileSpec.Builder.addFunction(
    name: String,
    crossinline builder: FunSpec.Builder.() -> Unit,
): FileSpec.Builder = addFunction(createFunction(name, builder))

internal fun FileSpec.Builder.addImport(
    type: KSType,
) = addImport(type.declaration.packageName.asString(), type.declaration.simpleName.asString())

internal inline fun <reified Type : Any> FileSpec.Builder.addImport() {
    val typeName = Type::class.asTypeName()
    addImport(typeName.packageName, typeName.simpleName)
}

// FunSpec helpers

internal inline fun createFunction(
    functionName: String,
    crossinline builder: FunSpec.Builder.() -> Unit,
): FunSpec = FunSpec.builder(functionName).apply(builder).build()

internal inline fun FunSpec.Builder.addCodeBlock(
    crossinline builder: CodeBlock.Builder.() -> Unit,
): FunSpec.Builder = addCode(buildCodeBlock(builder))

internal inline fun createConstructor(
    crossinline builder: FunSpec.Builder.() -> Unit,
): FunSpec = FunSpec.constructorBuilder().apply(builder).build()

internal fun FunSpec.Builder.addParameter(parameter: KSValueParameter) {
    val parameterName = parameter.name?.asString() ?: error("Parameter has no name.")
    addParameter(parameterName, parameter.type.toTypeName())
}

// TypeSpec helpers

internal inline fun createClass(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.classBuilder(className).apply(builder).build()

internal inline fun TypeSpec.Builder.addPrimaryConstructor(
    crossinline builder: FunSpec.Builder.() -> Unit,
): TypeSpec.Builder = primaryConstructor(createConstructor(builder))

internal fun TypeSpec.Builder.addProperty(
    name: String,
    type: KClass<*>,
    builder: PropertySpec.Builder.() -> Unit,
): TypeSpec.Builder = addProperty(createProperty(name, type, builder))

// PropertySpec helpers

internal fun createProperty(
    name: String,
    type: KClass<*>,
    builder: PropertySpec.Builder.() -> Unit,
) = PropertySpec.builder(name, type).apply(builder).build()

// Lambda helpers

internal inline fun <reified Type : Any> createLambda(
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName,
) = LambdaTypeName.get(Type::class.asTypeName(), parameters, returnType)
