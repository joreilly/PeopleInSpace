# PeopleInSpace

Minimal **Kotlin Multiplatform** project using Compose and SwiftUI.  Currently running on
* Android (Jetpack Compose)
* iOS (SwiftUI)
* watchOS (SwiftUI) (contributed by https://github.com/nealsanche)
* macOS (SwiftUI)
* Desktop (Compose for Desktop)
* Web (Compose for Web)
* Web (Kotlin/JS + React Wrapper) (contributed by https://github.com/PatilShreyas)
* JVM (small Ktor back end service + `Main.kt` in `common` module)

It makes use of [Open Notify PeopleInSpace API](http://open-notify.org/Open-Notify-API/People-In-Space/) to show list of people currently in
space and also the position of the International Space Station (inspired by https://kousenit.org/2019/12/19/a-few-astronomical-examples-in-kotlin/)!  

The project is included as sample in the official [Kotlin Multiplatform Mobile docs](https://kotlinlang.org/docs/mobile/samples.html#peopleinspace) and also the [Google Dev Library](https://devlibrary.withgoogle.com/products/android)

Related posts:
* [Minimal Kotlin Multiplatform project using Compose and SwiftUI](https://johnoreilly.dev/posts/minimal-kotlin-platform-compose-swiftui/)
* [Adding some Storage (to) Space](https://johnoreilly.dev/posts/adding-sqldelight-to-peopleinspace/)
* [Kotlin Multiplatform running on macOS](https://johnoreilly.dev/posts/kotlinmultiplatform-macos/)
* [PeopleInSpace hits the web with Kotlin/JS and React](https://johnoreilly.dev/posts/peopleinspace-kotlinjs/)
* [Using Koin in a Kotlin Multiplatform Project](https://johnoreilly.dev/posts/kotlinmultiplatform-koin/)
* [Jetpack Compose for the Desktop!](https://johnoreilly.dev/posts/jetpack-compose-desktop-copy/)
* [Comparing use of LiveData and StateFlow in a Jetpack Compose project](https://johnoreilly.dev/posts/jetpack-compose-stateflow-livedata/)
* [Wrapping Kotlin Flow with Swift Combine Publisher in a Kotlin Multiplatform project](https://johnoreilly.dev/posts/kotlinmultiplatform-swift-combine_publisher-flow/)
* [Using Swift Packages in a Kotlin Multiplatform project](https://johnoreilly.dev/posts/kotlinmultiplatform-swift-package/)


Note that this repository very much errs on the side of minimalism to help more clearly illustrate key moving parts of a Kotlin
Multiplatform project and also to hopefully help someone just starting to explore KMP to get up and running for first time (and is of course
primarily focussed on use of Jetpack Compose and SwiftUI).  If you're at stage of moving
beyond this then I'd definitely recommend checking out [KaMPKit](https://github.com/touchlab/KaMPKit) from Touchlab.
I also have the following samples that demonstrate the use of a variety of Kotlin Multiplatform libraries (and also use Jetpack Compose and SwiftUI).

* [BikeShare](https://github.com/joreilly/BikeShare)
* [GalwayBus](https://github.com/joreilly/GalwayBus)
* [MortyComposeKMM](https://github.com/joreilly/MortyComposeKMM)
* [FantasyPremierLeague](https://github.com/joreilly/FantasyPremierLeague)
* [StarWars](https://github.com/joreilly/StarWars)
* [Chip-8](https://github.com/joreilly/chip-8)


### Building
You need to use Android Studio Arctic Fox (**note: Java 11 is now the minimum version required**). Have tested with XCode v11 and v12.  

When opening iOS/watchOS/macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one). 

To exercise (React based) web client run `./gradlew :web:browserDevelopmentRun`.

To run backend you can either run `./gradlew :backend:run` or run `Server.kt` directly from Android Studio.

After doing that you should then for example be able to open `http://localhost:9090/astros_local.json` in a browser.



### Compose for Web client

The Compose for Web client resides in the `compose-web` module and can be run by
invoking `./gradlew :compose-web:jsBrowserDevelopmentRun`

### Compose for Desktop client

This client is available in `compose-desktop` module.  Note that you need to use appropriate version of JVM when running (works for example with Java 11)


### Screenshots 

iOS (SwiftUI)
<br/>
<img width="546" alt="Screenshot 2021-02-27 at 12 09 02" src="https://user-images.githubusercontent.com/6302/109386736-ac1f0700-78f4-11eb-812e-4bf971a8c2a7.png">

macOS (SwiftUI)
<br/>
<img width="937" alt="Screenshot 2021-06-01 at 20 02 31" src="https://user-images.githubusercontent.com/6302/120376983-6ec37e80-c314-11eb-8279-7acc0c2d5206.png">


Android (Jetpack Compose)
<br/>
<img width="555" alt="Screenshot 2021-03-07 at 17 03 46" src="https://user-images.githubusercontent.com/6302/110248059-2ab81c00-7f67-11eb-9b3a-2b04d1be43ef.png">

Compose for Desktop 
<br/>
<img width="555" alt="Screenshot 2021-03-07 at 17 05 31" src="https://user-images.githubusercontent.com/6302/110248116-69e66d00-7f67-11eb-8942-ab18fb8e48ff.png">

Compose for Web
<br/>
<img width="564" alt="Screenshot 2021-05-31 at 21 29 53" src="https://user-images.githubusercontent.com/6302/120240074-9dc7ea80-c257-11eb-9884-5870a3f4ef95.png">

Web App (Kotlin/JS + React)
<br/>
<img width="612" alt="Screenshot 2021-06-06 at 23 50 00" src="https://user-images.githubusercontent.com/19620536/120935764-eda82500-c721-11eb-9042-f15ade7473f7.png">




### Languages, libraries and tools used

* [Kotlin](https://kotlinlang.org/)
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor client library](https://github.com/ktorio/ktor)
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [Koin](https://github.com/InsertKoinIO/koin)
* [SQLDelight](https://github.com/cashapp/sqldelight)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* [SwiftUI](https://developer.apple.com/documentation/swiftui)
