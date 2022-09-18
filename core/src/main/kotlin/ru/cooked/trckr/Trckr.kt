package ru.cooked.trckr

class Trckr private constructor() {

    companion object {

        inline operator fun <reified T : Any> invoke(
            noinline builder: TrckrBuilder<T>.() -> Unit,
        ): T = new(T::class.java, builder)

        operator fun <T : Any> invoke(
            trackerClass: Class<T>,
            builder: TrckrBuilder<T>.() -> Unit,
        ): T = new(trackerClass, builder)

        inline fun <reified T : Any> new(
            noinline builder: TrckrBuilder<T>.() -> Unit,
        ): T = new(T::class.java, builder)

        fun <T : Any> new(
            trackerClass: Class<T>,
            builder: TrckrBuilder<T>.() -> Unit,
        ): T = TrckrBuilder(trackerClass).apply(builder).build()
    }
}
