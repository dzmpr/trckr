import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "ru.cookedapp.trckr"
version = "0.9.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trckr-core"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.squareup:kotlinpoet-ksp:1.12.0")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-opt-in=ru.cookedapp.trckr.core.annotations.internal.TrckrInternal"
    }
}
