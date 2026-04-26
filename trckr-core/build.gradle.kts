import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

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
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    // tvOS targets
    tvosArm64()
    tvosSimulatorArm64()
    // macOS targets
    macosArm64()
    // Linux targets
    linuxX64()
    linuxArm64()
    // MinGW targets
    mingwX64()

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true

        klib {
            enabled = true
        }

        filters {
            exclude {
                annotatedWith.add("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")
            }
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
