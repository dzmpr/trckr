import kotlinx.validation.ExperimentalBCVApi
import org.gradle.api.internal.catalog.DelegatingProjectDependency

plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryvalidator)
}

apiValidation {
    with(ignoredProjects) {
        add(projects.trckrDemo)
        add(projects.trckrProcessor)
    }
    nonPublicMarkers.add("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")

    @OptIn(ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}

dependencies {
    dokka(projects.trckrCore)
    dokka(projects.trckrProcessor)
}

fun MutableSet<String>.add(dependency: DelegatingProjectDependency) = add(dependency.name)
