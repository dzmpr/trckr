package ru.cooked.trckr.extensions

internal inline fun <reified T : Annotation> Array<out Annotation>.hasAnnotation(): Boolean {
    return any { annotation -> annotation is T }
}

internal inline fun <reified T: Annotation> Array<out Annotation>.getAnnotation(): T {
    return firstNotNullOf { annotation -> annotation as? T }
}

internal inline fun <reified T: Annotation> Array<out Annotation>.findAnnotation(): T? {
    return firstNotNullOfOrNull { annotation -> annotation as? T }
}
