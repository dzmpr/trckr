import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("trckr-artifact")
}

dependencies {
    implementation(project(":trckr-core"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-opt-in=ru.cookedapp.trckr.core.annotations.internal.TrckrInternal"
    }
}
