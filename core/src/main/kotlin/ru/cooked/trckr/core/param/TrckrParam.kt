package ru.cooked.trckr.core.param

import java.lang.reflect.Parameter
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.annotations.SkipIfNull
import ru.cooked.trckr.extensions.findAnnotation
import ru.cooked.trckr.extensions.getAnnotation

internal data class TrckrParam(
    val name: String,
    val skipIfNull: Boolean = false,
) {

    companion object {

        fun createParameter(parameter: Parameter): TrckrParam {
            val param = parameter.annotations.getAnnotation<Param>()
            val skipIfNull = parameter.annotations.findAnnotation<SkipIfNull>() != null
            return TrckrParam(param.name, skipIfNull)
        }
    }
}
