package ru.cooked.trckr

fun interface TrckrLogger {

    fun log(text: String)

    companion object {

        val DEFAULT by lazy(LazyThreadSafetyMode.NONE) {
            TrckrLogger { text -> println(text) }
        }
    }
}