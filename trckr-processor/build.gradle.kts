import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("trckr-artifact")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(projects.trckrCore)
            implementation(libs.ksp.api)
            implementation(libs.bundles.kotlinpoet)
        }
        jvmTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.bundles.compile.testing)
            }
            languageSettings {
                optIn("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
            }
        }
        all {
            languageSettings {
                optIn("ru.cookedapp.trckr.core.annotations.internal.TrckrInternal")
            }
        }
    }
}
