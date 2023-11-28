plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("trckr-artifact")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs += "-opt-in=ru.cookedapp.trckr.core.annotations.internal.TrckrInternal"
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":trckr-core"))
            implementation(libs.ksp.api)
            implementation(libs.bundles.kotlinpoet)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit)
            implementation(libs.mockk)
            implementation(libs.bundles.compile.testing)
        }
    }
}
