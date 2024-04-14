plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryvalidator)
}

apiValidation {
    with(ignoredProjects) {
        add(projects.trckrDemo.name)
        add(projects.trckrProcessor.name)
    }
    nonPublicMarkers.add("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")
}
