package ru.cookedapp.trckr.gradle

private const val NUMBER_PART = "(0|[1-9]\\d*)"
private const val VERSION_PART = "$NUMBER_PART\\.$NUMBER_PART\\.$NUMBER_PART"
private const val SUFFIX_PART = "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?"
private const val POSTFIX_PART = "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?"

// See https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
private val semanticVersionRegexp = ("^$VERSION_PART$SUFFIX_PART$POSTFIX_PART\$").toRegex()

fun isVersionStringValid(string: String): Boolean = semanticVersionRegexp.matches(string)
