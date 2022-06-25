package ru.cooked.trckr

import java.lang.reflect.Proxy
import ru.cooked.trckr.core.TrackerAdapter
import ru.cooked.trckr.core.TrckrInvocationHandler

class Trckr private constructor() {

    companion object {

        inline fun <reified T : Any> create(
            vararg adapters: TrackerAdapter,
            strictMode: TrckrStrictMode = TrckrStrictMode.WARNING,
            logger: TrckrLogger? = TrckrLogger.DEFAULT,
        ): T = create(
            trackerClass = T::class.java,
            strictMode = strictMode,
            logger = logger,
            adapters = adapters,
        )

        @Suppress("Unchecked_Cast")
        fun <T : Any> create(
            trackerClass: Class<T>,
            strictMode: TrckrStrictMode = TrckrStrictMode.WARNING,
            logger: TrckrLogger? = TrckrLogger.DEFAULT,
            vararg adapters: TrackerAdapter,
        ): T {
            if (!trackerClass.isInterface) {
                error("Tracker declaration must be an interface.")
            }
            val adaptersList = adapters.toList()
            val uniqueAdapters = getUniqueAdapters(strictMode, logger, adaptersList)

            return Proxy.newProxyInstance(
                trackerClass.classLoader,
                arrayOf(trackerClass),
                TrckrInvocationHandler(uniqueAdapters, strictMode, logger),
            ) as T
        }

        private fun getUniqueAdapters(
            strictMode: TrckrStrictMode,
            logger: TrckrLogger?,
            adapters: List<TrackerAdapter>,
        ): List<TrackerAdapter> {
            if (strictMode == TrckrStrictMode.NONE) return adapters

            val uniqueAdapterCount = adapters.groupBy { adapter -> adapter::class }.count()
            // TODO: show which adapters not unique
            if (adapters.size != uniqueAdapterCount) {
                if (strictMode == TrckrStrictMode.WARNING) {
                    logger?.log("Tracker adapters not unique.")
                } else if (strictMode == TrckrStrictMode.ERROR) {
                    error("Tracker adapters should be unique.")
                }
            }
            return adapters
        }
    }
}
