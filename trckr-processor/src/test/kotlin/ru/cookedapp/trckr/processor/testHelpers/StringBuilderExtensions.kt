package ru.cookedapp.trckr.processor.testHelpers

internal inline fun <reified T : Any> StringBuilder.addImport() {
    append("import ${T::class.qualifiedName};")
}
