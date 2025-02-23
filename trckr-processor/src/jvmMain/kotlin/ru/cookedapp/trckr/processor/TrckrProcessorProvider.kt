package ru.cookedapp.trckr.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class TrckrProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor = TrckrProcessor(
        logger = environment.logger,
        isKsp2 = environment.kspVersion.isSecondVersion,
        codeGenerator = environment.codeGenerator,
    )

    private val KotlinVersion.isSecondVersion: Boolean
        get() = this == KotlinVersion(major = 2, minor = 0, patch = 0)
}
