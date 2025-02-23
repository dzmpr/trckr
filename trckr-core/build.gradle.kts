import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("trckr-artifact")
}

kotlin {
    applyDefaultHierarchyTemplate()
    // JVM target
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    // JS targets
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    // watchOS targets
    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    // tvOS targets
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    // macOS targets
    macosX64()
    macosArm64()
    // Linux targets
    linuxX64()
    linuxArm64()
    // MinGW targets
    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
