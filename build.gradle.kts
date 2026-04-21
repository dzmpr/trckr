plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(projects.trckrCore)
    dokka(projects.trckrProcessor)
}
