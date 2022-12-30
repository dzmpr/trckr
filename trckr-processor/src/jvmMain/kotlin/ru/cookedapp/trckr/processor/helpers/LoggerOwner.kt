package ru.cookedapp.trckr.processor.helpers

import com.google.devtools.ksp.processing.KSPLogger

internal interface LoggerOwner {

    val logger: KSPLogger
}

internal fun LoggerOwner.logError(message: String) = logger.error(message)

internal fun LoggerOwner.logInfo(message: String) = logger.info(message)

internal fun LoggerOwner.logWarn(message: String) = logger.warn(message)
