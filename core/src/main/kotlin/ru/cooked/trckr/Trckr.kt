package ru.cooked.trckr

import java.lang.reflect.Proxy
import ru.cooked.trckr.core.TrackerAdapter
import ru.cooked.trckr.core.TrckrInvocationHandler

class Trckr private constructor() {

    companion object {

        inline fun <reified T : Any> create(
            vararg adapters: TrackerAdapter,
        ): T = create(trackerClass = T::class.java, adapters = adapters)

        @Suppress("Unchecked_Cast")
        fun <T : Any> create(
            trackerClass: Class<T>,
            vararg adapters: TrackerAdapter,
        ): T {
            if (!trackerClass.isInterface) error("Tracker declaration must be an interface.")

            return Proxy.newProxyInstance(
                trackerClass.classLoader,
                arrayOf(trackerClass),
                TrckrInvocationHandler(adapters.unique()),
            ) as T
        }

        private fun Array<out TrackerAdapter>.unique(): List<TrackerAdapter> {
            val uniqueAdapterCount = groupBy { adapter -> adapter::class }.count()
            if (size != uniqueAdapterCount) error("Tracker adapters should be unique.")
            return toList()
        }
    }
}
