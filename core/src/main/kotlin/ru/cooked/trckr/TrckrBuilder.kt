package ru.cooked.trckr

import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import ru.cooked.trckr.core.annotations.Event
import ru.cooked.trckr.core.annotations.Param
import ru.cooked.trckr.core.converter.ParamConverter
import ru.cooked.trckr.core.converter.PrimitivesConverter
import ru.cooked.trckr.core.adapter.TrackerAdapter
import ru.cooked.trckr.core.TrckrInvocationHandler
import ru.cooked.trckr.core.trckrError
import ru.cooked.trckr.extensions.hasAnnotation

class TrckrBuilder<T : Any> internal constructor(private val trackerClass: Class<T>) {

    init {
        verifyTrackerClass(trackerClass)
    }

    private val adapters = mutableMapOf<KClass<out TrackerAdapter>, TrackerAdapter>()
    private val converters = mutableMapOf<KClass<out ParamConverter>, ParamConverter>()

    fun addAdapter(adapter: TrackerAdapter) {
        if (adapters.containsKey(adapter::class)) {
            trckrError("Adapter \"${adapter::class.java.simpleName}\" already registered!")
        }
        adapters[adapter::class] = adapter
    }

    fun addConverter(converter: ParamConverter) {
        if (converters.containsKey(converter::class)) {
            trckrError("Converter \"${converter::class.java.simpleName}\" already registered!")
        }
        converters[converter::class] = converter
    }

    internal fun build(): T {
        addConverter(PrimitivesConverter())
        return createTrackerProxy()
    }

    @Suppress("Unchecked_Cast")
    private fun createTrackerProxy(): T = Proxy.newProxyInstance(
        trackerClass.classLoader,
        arrayOf(trackerClass),
        TrckrInvocationHandler(
            adaptersList = adapters.values.toList(),
            converters = converters.values.toList(),
        ),
    ) as T

    private fun verifyTrackerClass(trackerClass: Class<T>) {
        if (!trackerClass.isInterface) {
            trckrError("\"${trackerClass.simpleName}\" declaration must be an interface.")
        }
        trackerClass.methods.forEach { method ->
            verifyTrackerClassMethod(method)
        }
    }

    private fun verifyTrackerClassMethod(method: Method) {
        if (!method.annotations.hasAnnotation<Event>()) {
            trckrError("Tracker method \"${method.name}\" missing @Event annotation.")
        }
        if (method.returnType != Void.TYPE) {
            trckrError("Tracker method \"${method.name}\" shouldn't return anything.")
        }
        method.parameters.forEach { parameter ->
            verifyEventMethodParameter(method.name, parameter)
        }
    }

    private fun verifyEventMethodParameter(methodName: String, parameter: Parameter) {
        if (!parameter.annotations.hasAnnotation<Param>()) {
            trckrError("Tracker method \"$methodName\" has parameter without @Param annotation.")
        }
    }
}