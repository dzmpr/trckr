@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}
