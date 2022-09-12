package ru.cooked.trckr

import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import ru.cooked.trckr.core.TrckrInvocationHandler
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.converter.ParameterConverter
import ru.cooked.trckr.core.converter.PrimitivesConverter
import ru.cooked.trckr.core.converter.TypeConverter
import ru.cooked.trckr.core.ensureThat
import ru.cooked.trckr.extensions.hasAnnotation

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
            adaptersList = adapters.values.toList(),
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
    }
}
