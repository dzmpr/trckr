package ru.cookedapp.trckr.processor.helpers

import com.squareup.kotlinpoet.MemberName

internal object Imports {

    val emptyList = MemberName(packageName = "kotlin.collections", simpleName = "emptyList")

    val listOf = MemberName(packageName = "kotlin.collections", simpleName = "listOf")
}
