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

## Gradle setup

1. Add KSP plugin to your module's `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.8.22-1.0.11"
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
    implementation("ru.cookedapp.trckr:trckr-core:1.1.2")
    ksp("ru.cookedapp.trckr:trckr-processor:1.1.2")
}
```
4. Add KSP source path:

To access generated code from KSP, you need to set up the source path into your module's `build.gradle.kts` file:
```kotlin
kotlin {
    // ...
    sourceSets.configureEach {
        kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
    }
}
```

## Features

### Multimodule project support

Trckr supports multimodule projects.
You can define module-scoped trackers inside each module like this:
```kotlin
// Module alpha
interface ModuleAlphaTracker {

    @Event
    fun alphaEvent()
}

// Module beta
interface ModuleBetaTracker {
    
    @Event
    fun betaEvent()
}
```
And then, in application module you tie all trackers together:
```kotlin
@Tracker
interface ApplicationTracker : ModuleAlphaTracker, ModuleBetaTracker
```
**Trckr** generate `ApplicationTrackerImpl` for you, with all events from inherited tracker
interfaces. So you can create tracker and inject it with your DI framework to modules (or pass it manually).

### Skip certain adapters

It's not always needed to send event to all registered adapters. To use such methods in common tracker interface you can specify adapters that will be skipped in `@Event` annotation.
```kotlin
@Tracker
interface ExampleTracker {

    @Event(skipAdapters = [FirebaseAdapter::class])
    fun pay(@Param("amount") amount: Double)
}
```
```mermaid  
flowchart TD;
	E[ExampleTracker] --pay--> A;
    A[Trckr] --> B[Firebase];
    A --pay--> C[Amplitude] & D[Adjust];
 ```

### Skip null parameters

Nullable parameters by default converter would be converted to string "null". But if you want to skip such parameter you can set track strategy to `TrackStrategy.SKIP_IF_NULL`, and it will be sent only if value is not null.
```kotlin
@Tracker
interface ExampleTracker {
    
    @Event
    fun userSearch(
        @Param(
            name = "query",
            strategy = TrackStrategy.SKIP_IF_NULL,
        )
        query: String? = null,
    )
}
```

### Track null parameters

Parameter converter or type converter should convert value to any not null type. If you need to track null explicitly you can set track strategy to `TrackStrategy.TRACK_NULL`.
```kotlin
@Tracker
interface ExampleTracker {
    
    @Event
    fun userSearch(
        @Param(
            name = "query",
            strategy = TrackStrategy.TRACK_NULL,
        )
        query: String? = null,
    )
}
```

### Converters

To clean up call place you can register converter that would convert parameter value to desired format.

### Type converters

Type converter can convert parameter value based only on the value itself. It's suitable when you need to convert all values of certain type.
```kotlin
class EnumConverter : TypeConverter {
    
    fun convert(value: Any?): Any? {
        return if (value is Enum<*>) {
            value.name
        } else {
            null
        }
    }
}

val tracker = createExampleTracker {
    // ...
    addConverter(EnumConverter())
}
```

### Parameter converters

Parameter converter can convert parameter value based on event name, parameter name and passed value. It's allows to convert specific values, even if they are typed with type that converts to another representation by type converter, because trckr check parameter converters before type converters. 
```kotlin
@Tracker
interface ExampleTracker {
    
    @Event
    fun event(
        @Param("first") first: Int,
        @Param("second") second: Int,
    )
}

class FirstParameterConverter : ParameterConverter {
    
    fun convert(eventName: String, parameterName: String, value: Any?): Any? {
        return if (event == "event" && parameterName == "first") {
            val isPositive = (value as Int) > 0
            if (isPositive) "Positive" else "Negative"
        } else {
            null
        }
    }
}

val tracker = createExampleTracker {
    // ...
    addConverter(FirstConverter())
}
tracker.event(first = -10, second = 20)
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
