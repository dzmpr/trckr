@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.binaryvalidator)
}

apiValidation {
    with(ignoredProjects) {
        add("trckr-demo")
        add("trckr-processor")
    }
    nonPublicMarkers.add("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")
}

allprojects {
    repositories {
        mavenCentral()
    }
}
