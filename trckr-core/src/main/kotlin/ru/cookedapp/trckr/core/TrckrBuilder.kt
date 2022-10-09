package ru.cookedapp.trckr.core

import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import ru.cookedapp.trckr.core.adapter.TrackerAdapter
import ru.cookedapp.trckr.core.annotations.Event
import ru.cookedapp.trckr.core.annotations.Param
import ru.cookedapp.trckr.core.annotations.SkipIfNull
import ru.cookedapp.trckr.core.annotations.TrackNull
import ru.cookedapp.trckr.core.converter.ParameterConverter
import ru.cookedapp.trckr.core.converter.PrimitivesConverter
import ru.cookedapp.trckr.core.converter.TypeConverter
import ru.cookedapp.trckr.core.extensions.hasAnnotation

class TrckrBuilder<T : Any> internal constructor(private val trackerClass: Class<T>) {

    init {
        verifyTrackerClass(trackerClass)
    }

    private val adapters = mutableMapOf<KClass<out TrackerAdapter>, TrackerAdapter>()
    private val typeConverters = mutableMapOf<KClass<out TypeConverter>, TypeConverter>()
    private val parameterConverters = mutableMapOf<KClass<out ParameterConverter>, ParameterConverter>()

    fun addAdapter(adapter: TrackerAdapter) {
        ensureThat(!adapters.containsKey(adapter::class)) {
            "Adapter \"${adapter::class.simpleName}\" already registered!"
        }
        adapters[adapter::class] = adapter
    }

    fun addTypeConverter(converter: TypeConverter) {
        ensureThat(!typeConverters.containsKey(converter::class)) {
            "TypeConverter \"${converter::class.simpleName}\" already registered!"
        }
        typeConverters[converter::class] = converter
    }

    fun addParameterConverter(converter: ParameterConverter) {
        ensureThat(!parameterConverters.containsKey(converter::class)) {
            "ParameterConverter \"${converter::class.simpleName}\" already registered!"
        }
        parameterConverters[converter::class] = converter
    }

    internal fun build(): T {
        addTypeConverter(PrimitivesConverter())
        return createTrackerProxy()
    }

    @Suppress("Unchecked_Cast")
    private fun createTrackerProxy(): T = Proxy.newProxyInstance(
        trackerClass.classLoader,
        arrayOf(trackerClass),
        TrckrInvocationHandler(
            adapters = adapters.values.toList(),
            typeConverters = typeConverters.values.toList(),
            parameterConverters = parameterConverters.values.toList(),
        ),
    ) as T

    private fun verifyTrackerClass(trackerClass: Class<T>) {
        ensureThat(trackerClass.isInterface) {
            "\"${trackerClass.simpleName}\" declaration must be an interface."
        }
        trackerClass.methods.forEach { method ->
            verifyTrackerClassMethod(method)
        }
    }

    private fun verifyTrackerClassMethod(method: Method) {
        ensureThat(method.annotations.hasAnnotation<Event>()) {
            "Tracker method \"${method.name}\" missing @Event annotation."
        }
        ensureThat(method.returnType == Void.TYPE) {
            "Tracker method \"${method.name}\" shouldn't return anything."
        }
        method.parameters.forEach { parameter ->
            verifyEventMethodParameter(method.name, parameter)
        }
    }

    private fun verifyEventMethodParameter(methodName: String, parameter: Parameter) {
        ensureThat(parameter.annotations.hasAnnotation<Param>()) {
            "Tracker method \"$methodName\" has parameter without @Param annotation."
        }
        val hasSkipIfNull = parameter.annotations.hasAnnotation<SkipIfNull>()
        val hasTrackNull = parameter.annotations.hasAnnotation<TrackNull>()
        ensureThat(!hasSkipIfNull || !hasTrackNull) {
            "Parameter of \"$methodName\" should be annotated with @SkipIfNull either @TrackNull."
        }
    }
}
