@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("trckr-module")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":trckr-core"))
    ksp(project(":trckr-processor"))
}

sourceSets.configureEach {
    kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
}
