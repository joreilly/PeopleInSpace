# KMP-NativeCoroutines

A library to use Kotlin Coroutines from Swift code in KMP apps.

> **Warning**: you are viewing the documentation for the 1.0 pre-release version which is still a WIP.   
> The documentation for the 0.x releases can be found
> [here](https://github.com/rickclephas/KMP-NativeCoroutines/blob/master/README.md).  
> Looking to upgrade? Checkout the [migration steps](MIGRATING_TO_V1.md).

## Why this library?

Both KMP and Kotlin Coroutines are amazing, but together they have some limitations.

The most important limitation is cancellation support.  
Kotlin suspend functions are exposed to Swift as functions with a completion handler.  
This allows you to easily use them from your Swift code, but it doesn't support cancellation.

> **Note**: while Swift 5.5 brings async functions to Swift, it doesn't solve this issue.  
> For interoperability with ObjC all functions with a completion handler can be called like an async function.  
> This means starting with Swift 5.5 your Kotlin suspend functions will look like Swift async functions.  
> But that's just syntactic sugar, so there's still no cancellation support.

Besides cancellation support, ObjC doesn't support generics on protocols.  
So all the `Flow` interfaces lose their generic value type which make them hard to use.

This library solves both of these limitations 😄.

## Compatibility

The latest version of the library uses Kotlin version `1.8.0`.  
Compatibility versions for older Kotlin versions are also available:

| Version       | Version suffix  |   Kotlin   |    KSP    | Coroutines |
|---------------|-----------------|:----------:|:---------:|:----------:|
| **_latest_**  | **_no suffix_** | **1.8.0**  | **1.0.8** | **1.6.4**  |
| 1.0.0-ALPHA-3 | _no suffix_     | 1.8.0-RC-2 |   1.0.8   |   1.6.4    |
| 1.0.0-ALPHA-2 | _no suffix_     |  1.8.0-RC  |   1.0.8   |   1.6.4    |
| 1.0.0-ALPHA-1 | _no suffix_     | 1.8.0-Beta |   1.0.8   |   1.6.4    |

You can choose from a couple of Swift implementations.  
Depending on the implementation you can support as low as iOS 9, macOS 10.9, tvOS 9 and watchOS 3:

| Implementation | Swift | iOS  | macOS | tvOS | watchOS |
|----------------|:-----:|:----:|:-----:|:----:|:-------:|
| Async          |  5.5  | 13.0 | 10.15 | 13.0 |   6.0   |
| Combine        |  5.0  | 13.0 | 10.15 | 13.0 |   6.0   |
| RxSwift        |  5.0  | 9.0  | 10.9  | 9.0  |   3.0   |

## Installation

The library consists of a Kotlin and Swift part which you'll need to add to your project.  
The Kotlin part is available on Maven Central and the Swift part can be installed via CocoaPods 
or the Swift Package Manager.

Make sure to always use the same versions for all the libraries!

[![latest release](https://img.shields.io/github/v/release/rickclephas/KMP-NativeCoroutines?label=latest%20release&sort=semver)](https://github.com/rickclephas/KMP-NativeCoroutines/releases)

### Kotlin

For Kotlin just add the plugin to your `build.gradle.kts`:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "<ksp-version>"
    id("com.rickclephas.kmp.nativecoroutines") version "<version>"
}
```
and make sure to opt in to the experimental `@ObjCName` annotation:
```kotlin
kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}
```

### Swift (Swift Package Manager)

The Swift implementations are available via the Swift Package Manager.  
Just add it to your `Package.swift` file:
```swift
dependencies: [
    .package(url: "https://github.com/rickclephas/KMP-NativeCoroutines.git", from: "<version>")
]
```

Or add it in Xcode by going to `File` > `Add Packages...` and providing the URL:
`https://github.com/rickclephas/KMP-NativeCoroutines.git`.

> **Note**: the version for the Swift package should not contain the Kotlin version suffix
> (e.g. `-new-mm` or `-kotlin-1.6.0`).

> **Note**: if you only need a single implementation you can also use the SPM specific versions with suffixes
> `-spm-async`, `-spm-combine` and `-spm-rxswift`.

### Swift (CocoaPods)

If you use CocoaPods add one or more of the following libraries to your `Podfile`:
```ruby
pod 'KMPNativeCoroutinesAsync', '<version>'    # Swift Concurrency implementation
pod 'KMPNativeCoroutinesCombine', '<version>'  # Combine implementation
pod 'KMPNativeCoroutinesRxSwift', '<version>'  # RxSwift implementation
```
> **Note**: the version for CocoaPods should not contain the Kotlin version suffix (e.g. `-new-mm` or `-kotlin-1.6.0`).

## Usage

Using your Kotlin Coroutines code from Swift is almost as easy as calling the Kotlin code.   
Just use the wrapper functions in Swift to get async functions, AsyncStreams, Publishers or Observables.

### Kotlin

The plugin will automagically generate the necessary code for you! 🔮  
Just annotate your coroutines declarations with `@NativeCoroutines` (or `@NativeCoroutinesState`).

#### Flows

Your `Flow` properties/functions get a native version:
```kotlin
class Clock {
    // Somewhere in your Kotlin code you define a Flow property
    // and annotate it with @NativeCoroutines
    @NativeCoroutines
    val time: StateFlow<Long> // This can be any kind of Flow
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate this native property for you:
```kotlin
@ObjCName(name = "time")
val Clock.timeNative
    get() = time.asNativeFlow()
```

For the `StateFlow` defined above the plugin will also generate this value property:
```kotlin
val Clock.timeValue
    get() = time.value
```

In case of a `SharedFlow` the plugin would generate a replay cache property:
```kotlin
val Clock.timeReplayCache
    get() = time.replayCache
```
</p>
</details>

#### StateFlows

Using `StateFlow` properties to track state (like in a view model)?  
Use the `@NativeCoroutinesState` annotation instead:
```kotlin
class Clock {
    // Somewhere in your Kotlin code you define a StateFlow property
    // and annotate it with @NativeCoroutinesState
    @NativeCoroutinesState
    val time: StateFlow<Long> // This must be a StateFlow
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate these native properties for you:
```kotlin
@ObjCName(name = "time")
val Clock.timeValue
    get() = time.value

val Clock.timeFlow
    get() = time.asNativeFlow()
```
</p>
</details>

#### Suspend functions

The plugin also generates native versions for your annotated suspend functions:
```kotlin
class RandomLettersGenerator {
    // Somewhere in your Kotlin code you define a suspend function
    // and annotate it with @NativeCoroutines
    @NativeCoroutines
    suspend fun getRandomLetters(): String { 
        // Code to generate some random letters
    }
}
```

<details><summary>Generated code</summary>
<p>

The plugin will generate this native function for you:
```kotlin
@ObjCName(name = "getRandomLetters")
fun RandomLettersGenerator.getRandomLettersNative() =
    nativeSuspend { getRandomLetters() }
```
</p>
</details>

### Swift Concurrency

The Async implementation provides some functions to get async Swift functions and `AsyncSequence`s.

#### Async functions

Use the `asyncFunction(for:)` function to get an async function that can be awaited:
```swift
let handle = Task {
    do {
        let letters = try await asyncFunction(for: randomLettersGenerator.getRandomLetters())
        print("Got random letters: \(letters)")
    } catch {
        print("Failed with error: \(error)")
    }
}

// To cancel the suspend function just cancel the async task
handle.cancel()
```

or if you don't like these do-catches you can use the `asyncResult(for:)` function:
```swift
let result = await asyncResult(for: randomLettersGenerator.getRandomLetters())
if case let .success(letters) = result {
    print("Got random letters: \(letters)")
}
```

#### AsyncSequence

For `Flow`s there is the `asyncSequence(for:)` function to get an `AsyncSequence`:
```swift
let handle = Task {
    do {
        let sequence = asyncSequence(for: randomLettersGenerator.getRandomLettersFlow())
        for try await letters in sequence {
            print("Got random letters: \(letters)")
        }
    } catch {
        print("Failed with error: \(error)")
    }
}

// To cancel the flow (collection) just cancel the async task
handle.cancel()
```

### Combine

The Combine implementation provides a couple functions to get an `AnyPublisher` for your Coroutines code.

> **Note**: these functions create deferred `AnyPublisher`s.  
> Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

#### Publisher

For your `Flow`s use the `createPublisher(for:)` function:
```swift
// Create an AnyPublisher for your flow
let publisher = createPublisher(for: clock.time)

// Now use this publisher as you would any other
let cancellable = publisher.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the flow (collection) just cancel the publisher
cancellable.cancel()
```

You can also use the `createPublisher(for:)` function for suspend functions that return a `Flow`:
```swift
let publisher = createPublisher(for: randomLettersGenerator.getRandomLettersFlow())
```

#### Future

For the suspend functions you should use the `createFuture(for:)` function:
```swift
// Create a Future/AnyPublisher for the suspend function
let future = createFuture(for: randomLettersGenerator.getRandomLetters())

// Now use this future as you would any other
let cancellable = future.sink { completion in
    print("Received completion: \(completion)")
} receiveValue: { value in
    print("Received value: \(value)")
}

// To cancel the suspend function just cancel the future
cancellable.cancel()
```

### RxSwift

The RxSwift implementation provides a couple functions to get an `Observable` or `Single` for your Coroutines code.

> **Note**: these functions create deferred `Observable`s and `Single`s.  
> Meaning every subscription will trigger the collection of the `Flow` or execution of the suspend function.

#### Observable

For your `Flow`s use the `createObservable(for:)` function:
```swift
// Create an observable for your flow
let observable = createObservable(for: clock.time)

// Now use this observable as you would any other
let disposable = observable.subscribe(onNext: { value in
    print("Received value: \(value)")
}, onError: { error in
    print("Received error: \(error)")
}, onCompleted: {
    print("Observable completed")
}, onDisposed: {
    print("Observable disposed")
})

// To cancel the flow (collection) just dispose the subscription
disposable.dispose()
```

You can also use the `createObservable(for:)` function for suspend functions that return a `Flow`:
```swift
let observable = createObservable(for: randomLettersGenerator.getRandomLettersFlow())
```

#### Single

For the suspend functions you should use the `createSingle(for:)` function:
```swift
// Create a single for the suspend function
let single = createSingle(for: randomLettersGenerator.getRandomLetters())

// Now use this single as you would any other
let disposable = single.subscribe(onSuccess: { value in
    print("Received value: \(value)")
}, onFailure: { error in
    print("Received error: \(error)")
}, onDisposed: {
    print("Single disposed")
})

// To cancel the suspend function just dispose the subscription
disposable.dispose()
```

## Customize

There are a number of ways you can customize the generated Kotlin code.

### Name suffix

Don't like the naming of the generated properties/functions?  
Specify your own custom suffixes in your `build.gradle.kts` file:
```kotlin
nativeCoroutines {
    // The suffix used to generate the native coroutine function and property names.
    suffix = "Native"
    // The suffix used to generate the native coroutine file names.
    // Note: defaults to the suffix value when `null`.
    fileSuffix = null
    // The suffix used to generate the StateFlow value property names,
    // or `null` to remove the value properties.
    flowValueSuffix = "Value"
    // The suffix used to generate the SharedFlow replayCache property names,
    // or `null` to remove the replayCache properties.
    flowReplayCacheSuffix = "ReplayCache"
    // The suffix used to generate the native state property names.
    stateSuffix = "Value"
    // The suffix used to generate the `StateFlow` flow property names,
    // or `null` to remove the flow properties.
    stateFlowSuffix = "Flow"
}
```

### CoroutineScope

For more control you can provide a custom `CoroutineScope` with the `NativeCoroutineScope` annotation:
```kotlin
class Clock {
    @NativeCoroutineScope
    internal val coroutineScope = CoroutineScope(job + Dispatchers.Default)
}
```

> **Note**: your custom coroutine scope must be either `internal` or `public`.

If you don't provide a `CoroutineScope` the default scope will be used which is defined as:
```kotlin
internal val defaultCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
```

> **Note**: KMP-NativeCoroutines has built-in support for [KMM-ViewModel](https://github.com/rickclephas/KMM-ViewModel).  
> Coroutines inside your `KMMViewModel` will (by default) use the `CoroutineScope` from the `ViewModelScope`. 

### Ignoring declarations

Use the `NativeCoroutinesIgnore` annotation to tell the plugin to ignore a property or function:
```kotlin
@NativeCoroutinesIgnore
val ignoredFlowProperty: Flow<Int>

@NativeCoroutinesIgnore
suspend fun ignoredSuspendFunction() { }
```
