import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryvalidator)
}

tasks.withType<DokkaMultiModuleTask>().named {
    it.contains("Html")
}.configureEach {
    outputDirectory.set(rootProject.layout.projectDirectory.dir("docs/api"))
}

apiValidation {
    with(ignoredProjects) {
        add("trckr-demo")
        add("trckr-processor")
    }
    nonPublicMarkers.add("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")
}
