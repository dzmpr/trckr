import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trckr-core"))
    ksp(project(":trckr-processor"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets.configureEach {
    kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
}
