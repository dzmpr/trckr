To run **trckr** you need to add KSP to your project:

In module `build.gradle.kts` you need to apply KSP plugin and specify maven central repository:

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "$ksp_version"
    // Or if you use gradle version catalog
    alias(libs.plugins.ksp)
}

repositories {
    // ...
    mavenCentral()
}
```

[KSP](https://github.com/google/ksp) is used to generate implementation code from your tracker declarations. Both KSP1 and KSP2 are supported.

Then you need to add **trckr** dependencies:

### Single platform project

```kotlin
dependencies {
    // ...
    implementation("ru.cookedapp.trckr:trckr-core:$trckr_version")
    ksp("ru.cookedapp.trckr:trckr-processor:$trckr_version")
}
```

### KMP project

Adding **trckr** to common target allows you to declare tracker interface inside the common code and then create tracker with platform specific adapters in other targets. 

```kotlin
kotlin {
    sourceSets {
        androidMain.dependencies {
            // ..
            ksp("ru.cookedapp.trckr:trckr-processor:$trckr_version")
        }
        commonMain.dependencies {
            // ...
            implementation("ru.cookedapp.trckr:trckr-core:$trckr_version")
        }
    }
}
```

Then to set your IDE aware of KSP-generated code you need to set up the source path into your module's `build.gradle.kts` file:

```kotlin
kotlin {
// ...
    sourceSets.configureEach {
        kotlin.srcDir(layout.buildDirectory.dir("/generated/ksp/$name/kotlin/"))
    }
}
```
