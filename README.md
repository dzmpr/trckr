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
fun ExampleTracker {
    ...

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
Nullable parameters by default converts to string as "null". But if you want to skip such parameter you can annotate it with `@SkipIfNull` annotation and it will be send only if value is not null.
```kotlin
fun ExampleTracker {
    ...
    
    @Event
    fun userSearch(
        @SkipIfNull
        @Param("query")
        query: String? = null,
    )
}
```
### Parameter converters
To cleanup call place you can register parameter converter that would convert parameter value to desired string format. You can convert all values of specific type or just convert individual parameter based on event and parameter names. By default trckr converts `Long`, `Int`, `Float`, `Double`, `Boolean` to it's string representation and `null` to `"null"` string.
```kotlin
class EnumConverter: GenericParamConverter() {
    
    fun convert(value: Any?): String? {
        return if (value is Enum<*>) {
            value.name
        } else {
            super.convert(value)
        }
    }
}

val tracker: ExampleTracker = Trckr.new {
    ...
    addConverter(EnumConverter())
}
```
