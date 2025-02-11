When migrating to **trckr** you need to get familiar with its terminology.

Entry point of **trckr** is a _tracker interface_ where you specify events, its names and parameters.

```kotlin
@Tracker
interface ProductAnalytics {
    
    @Event(name = "payment_completed")
    fun paymentCompleted(
        @Param(name = "amount") amount: Int,
    )
}
```

!!! --- "Note"
    If event name was not specified **trckr** will use event method name as event name. But in case you use minification in your project be careful that event name can change in process of code minification. So I recommend always explicitly specify event name.

Based on this interface **trckr** will generate tracker implementation which you don't have direct access to. You can obtain instance of tracker using `create*TrackerName*` build (`createProductAnalytics` in this example). Using this builder you should supply tracker with _tracker adapters_:

```kotlin
val tracker = createProductAnalytics {
    addAdapter(FirstBackendAdapter())
    addAdapter(SecondBackendAdapter())
    // and so on ...
}
```

What is tracker adapter? It's a class, responsible for sending events to specific analytics backend. Adapter receive from tracker implementation `eventName` and map with event `parameters` (if it's present). Here is sample adapter implementation:
```kotlin
class FirstBackendAdapter(
    fbs: FirstBackendService, 
) : TrackerAdapter {
    
    override fun trackEvent(eventName: String, parameters: Map<String, Any?>) {
        verifyEventName(eventName)
        verifyParameters(parameters)
        fbs.sendEvent(eventName, json.toJson(parameters))
    }
}
```
In adapter, you could perform backend-specific events' manipulation. For example when sending events to firebase you need to ensure event name consists only from alphanumeric characters and underscores and has no whitespaces. Also, event name should not be longer than 40 characters.

## Migration

Consider you have simple multi-backend setup:

```kotlin
// TrackerService.kt
interface TrackerService {

    fun firstEvent()

    fun secondEvent(param: Int)
}

// CompositeTrackerService.kt
class CompositeTrackerService(
    private val trackers: List<TrackerService>,
) : TrackerService {

    constructor(vararg trackers: TrackerService) : this(trackers.toList())

    override fun firstEvent() {
        trackers.forEach {
            it.firstEvent()
        }
    }

    override fun secondEvent(param: Int) {
        trackers.forEach {
            it.secondEvent(param)
        }
    }
}

// FirstBackendTrackerService.kt
class FirstBackendTrackerService : TrackerService {

    override fun firstEvent() { /* first backend tracking logic */
    }

    override fun secondEvent(param: Int) { /* first backend tracking logic */
    }
}

// SecondBackendTrackerService.kt
class SecondBackendTrackerService : TrackerService {

    override fun firstEvent() { /* second backend tracking logic */
    }

    override fun secondEvent(param: Int) { /* second backend tracking logic */
    }
}
```

### Migrate tracker interface

At the begging your `TrackerService` becomes a tracker. You need to add trckr annotations:

```kotlin
@Tracker
interface TrackerService {
    
    @Event(name = "firstEvent")
    fun firstEvent()

    @Event(name = "secondEvent")
    fun secondEvent(@Param(name = "param") param: Int)
}
```

### Migrate tracking logic

Then you need convert classes, that responsible for tracking events to concrete backends to adapters:
```kotlin
class FirstBackendTrackerService : TrackerAdapter {

//    override fun firstEvent() { /* first backend tracking logic */ }
//    
//    override fun secondEvent(param: Int) { /* first backend tracking logic */ }
    
    override fun trackEvent(eventName: String, parameters: Map<String, Any>?) {
        /* common first backend tracking logic */
    }
}
```

### Migrate tracker instantiation

After that you need to update tracker instantiation logic. You no longer need `CompositeTrackerService` class. In an example I migrate dagger/hilt provides function that instantiate tracker: 

```kotlin
@Provides
fun provideTrackerService(
    first: FirstBackendTrackerService,
    second: SecondBackendTrackerService,
): TrackerService {
//    return CompositeTrackerService(first, second)
    return createTrackerService {
        add(first)
        add(second)
    }
}
```

And now migration is complete!
