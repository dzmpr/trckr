@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("trckr-artifact")
}

kotlin {
    // Targets

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
    // Currently not support arrays in annotations
    // js(BOTH) {
    //     browser()
    //     nodejs()
    // }
    // iOS targets
    iosX64()
    iosArm64()
    iosArm32()
    iosSimulatorArm64()
    // watchOS targets
    watchosArm32()
    watchosArm64()
    watchosX86()
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
    linuxArm32Hfp()
    linuxMips32()
    linuxMipsel32()
    linuxX64()
    // MinGW targets
    mingwX64()
    mingwX86()

    // SourceSets
    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
