package ru.cookedapp.trckr.core.param

import java.lang.reflect.Parameter
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.SkipIfNull
import ru.cookedapp.trckr.core.annotations.TrackNull
import ru.cookedapp.trckr.core.extensions.findAnnotation
import ru.cookedapp.trckr.core.extensions.getAnnotation

internal data class TrckrParam(
    val name: String,
    val skipIfNull: Boolean,
    val trackNull: Boolean,
) {

    companion object {

        fun createParameter(parameter: Parameter): TrckrParam {
            with(parameter.annotations) {
                val param = getAnnotation<Param>()
                val skipIfNull = findAnnotation<SkipIfNull>() != null
                val trackNull = findAnnotation<TrackNull>() != null
                return TrckrParam(param.name, skipIfNull, trackNull)
            }
        }
    }
}
