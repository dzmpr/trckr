# trckr

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.cookedapp.trckr/trckr-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ru.cookedapp.trckr/trckr-core)
[![CI](https://github.com/dzmpr/trckr/actions/workflows/tests.yml/badge.svg)](https://github.com/dzmpr/trckr/actions/workflows/tests.yml)
[![KMM](https://img.shields.io/badge/KMM-supported-orange)](https://kotlinlang.org/docs/multiplatform-mobile-getting-started.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Trckr is **Kotlin Symbol Processor** to help you send analytics to multiple destinations (called adapters).

## Why trckr?

In a large project you can have multiple analytics targets that should receive events from your app. That creates a lot of boilerplate code to implement tracker that uses all analytics targets.
Trckr created to eliminate this problem. You need to create target adapters just once, and trckr generate events from interface methods for you.
To declare tracker you define interface, annotated with `@Tracker` annotation and event methods with `@Event` annotation:  
```kotlin 
@Tracker
interface ExampleTracker {

    @Event(name = "Event name")
    fun event(
        @Param(name = "Parameter name") data: Int,
    )
}
```  
And create instance of tracker using generated method:  
```kotlin  
val tracker = createExampleTracker {  
    addAdapter(FirebaseAdapter())
    addAdapter(AmplitudeAdapter())
    addAdapter(AdjustAdapter())
}
tracker.event(data = 42)
```
After calling event method trckr sends it to all registered adapters.
```mermaid  
flowchart TD;
	E[ExampleTracker] --event--> A;
    A[Trckr] --event--> B[Firebase] & C[Amplitude] & D[Adjust];
 ```

## Key features

* Adapters skipping
* Parameter converters support
* Multimodule project support

More you can find at [advanced features](https://dzmpr.github.io/trckr/advanced_features/) page.

## Gradle setup

1. Add KSP plugin to your module's `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}
```
2. Add `Maven Central` to the repositories blocks in your project's `build.gradle.kts`:
```kotlin
repositories {
    mavenCentral()
}
```
3. Add `trckr` dependencies:
```kotlin
dependencies {
    implementation("ru.cookedapp.trckr:trckr-core:1.1.5")
    ksp("ru.cookedapp.trckr:trckr-processor:1.1.5")
}
```
4. Add KSP source path:

To access generated code from KSP, you need to set up the source path into your module's `build.gradle.kts` file:
```kotlin
kotlin {
    // ...
    sourceSets.configureEach {
        kotlin.srcDir(layout.buildDirectory.dir("/generated/ksp/$name/kotlin/"))
    }
}
```

## License

```text
Copyright 2022 Dzmitry Pryskoka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
