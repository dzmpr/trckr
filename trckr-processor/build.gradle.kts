import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("trckr-artifact")
}

dependencies {
    implementation(project(":trckr-core"))
    implementation(libs.ksp.api)
    implementation(libs.bundles.kotlinpoet)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.compile.testing)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-opt-in=ru.cookedapp.trckr.core.annotations.internal.TrckrInternal"
    }
}
