package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class TrckrProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor = TrckrProcessor(
        environment.options,
        environment.codeGenerator,
        environment.logger,
    )
}
