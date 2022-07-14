package ru.cooked.trckr.core

import java.lang.reflect.Method
import ru.cooked.trckr.annotations.Param
import ru.cooked.trckr.annotations.SkipIfNull
import ru.cooked.trckr.extensions.findAnnotation
import ru.cooked.trckr.extensions.getAnnotation

internal data class ParamInternal(
    val name: String,
    val skipIfNull: Boolean = false,
) {

    companion object {

        fun createParameters(
            method: Method,
        ): List<ParamInternal> = method.parameters.map { parameter ->
            createParameter(parameter.annotations)
        }

        private fun createParameter(annotations: Array<Annotation>): ParamInternal {
            val param = annotations.getAnnotation<Param>()
            val skipIfNull = annotations.findAnnotation<SkipIfNull>() != null
            return ParamInternal(param.name, skipIfNull)
        }
    }
}
