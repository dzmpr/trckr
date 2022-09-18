# trckr  
Trckr uses Java Dynamic Proxy to proxy events into the appropriate adapters.  
Adapter receives event name and map of passed parameters that are can be used to track actual event. To declare tracker you need create  
interface and annotate methods with `@Event` annotation:  
```kotlin  
interface ExampleTracker {

    @Event(name = "Event name")
    fun event(
        @Param(name = "Parameter name") data: Int,
    )
}
```  
And create instance of tracker using trckr builder:  
```kotlin  
val tracker: ExampleTracker = Trckr.new {  
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
## Features
### Skip certain adapters
It's not always needed to send event to all registered adapters. To use such methods in common tracker interface you can annotate it with `@SkipAdapters` annotation and pass adapter classes that should be skipped.
```kotlin
interface ExampleTracker {

    @Event
    @SkipAdapters(FirebaseAdapter::class)
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
Nullable parameters by default converts to string as "null". But if you want to skip such parameter you can annotate it with `@SkipIfNull` annotation, and it will be sent only if value is not null.
```kotlin
interface ExampleTracker {
    
    @Event
    fun userSearch(
        @SkipIfNull
        @Param("query")
        query: String? = null,
    )
}
```
### Track null parameters
Parameter converter or type converter should convert value to any not null type. If you need to null will be tracked explicitly you can annotate it with `@TrackNull` annotation.
```kotlin
interface ExampleTracker {
    
    @Event
    fun userSearch(
        @TrackNull
        @Param("query")
        query: String? = null,
    )
}
```
## Converters
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

val tracker: ExampleTracker = Trckr.new {
    ...
    addConverter(EnumConverter())
}
```
### Parameter converters
Parameter converted can convert parameter value based on event name, parameter name and passed value. It's allows to convert specific values, even if they are typed with type that converts to another representation by type parameter. 
```kotlin
interface ExampleTracker {
    
    @Event
    fun event(
        @Param("first") first: Int,
        @Param("second") second: Int,
    )
}

class FirstConverter : ParameterConverter {
    
    fun convert(eventName: String, parameterName: String, value: Any?): Any? {
        return if (event == "event" && parameterName == "first") {
            val isPositive = (value as Int) > 0
            if (isPositive) "Positive" else "Negative"
        } else {
            null
        }
    }
}

val tracker: ExampleTracker = Trckr.new {
    ...
    addConverter(FirstConverter())
}
tracker.event(first = -10, second = 20)
```
