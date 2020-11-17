# PeopleInSpace

Minimal **Kotlin Multiplatform** project using Jetpack Compose and SwiftUI.  Currently running on
* Android (Jetpack Compose)
* iOS (SwiftUI)
* watchOS (SwiftUI)
* macOS (SwiftUI)
* Desktop (Jetpack Compose for Desktop)
* Web (Kotlin/JS + React Wrapper)
* JVM (small Ktor back end service)

It makes use of basic API (http://open-notify.org/Open-Notify-API/People-In-Space/) to show list of people currently in
space (inspired by https://kousenit.org/2019/12/19/a-few-astronomical-examples-in-kotlin/)!  The list is shown on Android
using **Jetpack Compose**, on iOS using **SwiftUI** and on Web using Kotlin/JS React wrapper.

Related posts:
* [Minimal Kotlin Multiplatform project using Compose and SwiftUI](https://johnoreilly.dev/posts/minimal-kotlin-platform-compose-swiftui/)
* [Adding some Storage (to) Space](https://johnoreilly.dev/posts/adding-sqldelight-to-peopleinspace/)
* [Kotlin Multiplatform running on macOS](https://johnoreilly.dev/posts/kotlinmultiplatform-macos/)
* [PeopleInSpace hits the web with Kotlin/JS and React](https://johnoreilly.dev/posts/peopleinspace-kotlinjs/)
* [Using Koin in a Kotlin Multiplatform Project](https://johnoreilly.dev/posts/kotlinmultiplatform-koin/)
* [Jetpack Compose for the Desktop!](https://johnoreilly.dev/posts/jetpack-compose-desktop/)


Note that this repository very much errs on the side of mimimalism to help more clearly illustrate key moving parts of a Koltin
Multiplatform project and also to hopefully help someone just starting to explore KMP to get up and running for first time (and is of course
primarily focussed on use of Jetpack Compose and SwiftUI).  If you're at stage of moving
beyond this then I'd definitely recommend checking out [KaMPKit](https://github.com/touchlab/KaMPKit)


### Building
You need to use Android Studio v4.2 (currently preview/canary version).  Have tested on XCode v11 and v12.  When opening
iOS/watchOS/macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one). To exercise web client run `./gradlew :web:browserDevelopmentRun`.

### Jetpack Compose for Desktop client

This client is available in `compose-desktop` module.  Note that you currently need to use EAP version of kotlin
plugin and also use appropriate JVM when running (works for example with Java 11)

### Languages, libraries and tools used

* [Kotlin](https://kotlinlang.org/)
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor client library](https://github.com/ktorio/ktor)
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [Koin](https://github.com/InsertKoinIO/koin)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [SwiftUI](https://developer.apple.com/documentation/swiftui)
