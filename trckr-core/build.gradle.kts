import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("trckr-artifact")
}

kotlin {
    // Targets
    applyDefaultHierarchyTemplate()
    // JVM target
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    // JS target
    // TODO: Currently not support arrays in annotations
    // js(IR) {
    //     browser()
    //     nodejs()
    // }
    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    // watchOS targets
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    // tvOS targets
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    // macOS targets
    macosX64()
    macosArm64()
    // Linux targets
    linuxArm64()
    linuxX64()
    // MinGW targets
    mingwX64()

    // SourceSets
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
