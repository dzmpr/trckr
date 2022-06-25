package ru.cooked.trckr

class Trckr private constructor() {

    companion object {

        inline fun <reified T : Any> new(
            noinline builder: TrckrBuilder<T>.() -> Unit,
        ): T = new(T::class.java, builder)

        fun <T : Any> new(
            trackerClass: Class<T>,
            builder: TrckrBuilder<T>.() -> Unit,
        ): T = TrckrBuilder(trackerClass).apply(builder).build()
    }
}
