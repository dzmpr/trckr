package ru.cooked.trckr.core

import java.lang.reflect.Method
import ru.cooked.trckr.annotations.Param
import ru.cooked.trckr.annotations.SkipIfNull

internal data class ParamInternal(
    val name: String,
    val skipIfNull: Boolean = false,
) {

    companion object {

        fun createParameters(
            method: Method,
        ): List<ParamInternal> = method.parameters.map { parameter ->
            createParameter(method.name, parameter.annotations)
        }

        private fun createParameter(methodName: String, annotations: Array<Annotation>): ParamInternal {
            val param = annotations.find { it is Param } as? Param
                ?: error("Tracker method \"$methodName\" has parameter with missing @Param annotation.")
            val skipIfNull = annotations.find { it is SkipIfNull } != null
            return ParamInternal(param.name, skipIfNull)
        }
    }
}
