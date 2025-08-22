# PeopleInSpace

![kotlin-version](https://img.shields.io/badge/kotlin-2.2.0-blue?logo=kotlin)


**Kotlin Multiplatform** project with SwiftUI, Jetpack Compose, Compose for Wear OS, Compose for Desktop and Compose for Web clients along with Ktor backend. Currently running on
* Android (Jetpack Compose)
* Android App Widget (Compose based Glance API - contributed by https://github.com/yschimke)
* Wear OS (Compose for Wear OS - primarily developed by https://github.com/yschimke)  
* iOS (SwiftUI)
* Swift Executable Package
* Desktop (Compose for Desktop)
* Web (Compose for Web - Wasm based)
* JVM (small Ktor back end service + `Main.kt` in `common` module)
* MCP server (using same shared KMP code)

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
* [Using Swift's new async/await when invoking Kotlin Multiplatform code](https://johnoreilly.dev/posts/swift_async_await_kotlin_coroutines/)
* [Exploring new AWS SDK for Kotlin](https://johnoreilly.dev/posts/aws-sdk-kotlin/)
* [Creating a Swift command line app that consumes Kotlin Multiplatform code](https://johnoreilly.dev/posts/swift-command-line-kotlin-multiplatform/)
* [Exploring New Worlds of UI sharing possibilities in PeopleInSpace using Compose Multiplatform](https://johnoreilly.dev/posts/exploring-compose_multiplatform_sharing_ios/)


Note that this repository very much errs on the side of minimalism to help more clearly illustrate key moving parts of a Kotlin
Multiplatform project and also to hopefully help someone just starting to explore KMP to get up and running for first time (and is of course
primarily focused on use of Jetpack Compose and SwiftUI).  If you're at the stage of moving
beyond this then I'd definitely recommend checking out [KaMPKit](https://github.com/touchlab/KaMPKit) from Touchlab.
I also have the following samples that demonstrate the use of a variety of Kotlin Multiplatform libraries (and also use Jetpack Compose and SwiftUI).

*  GalwayBus (https://github.com/joreilly/GalwayBus)
*  Confetti (https://github.com/joreilly/Confetti)
*  BikeShare (https://github.com/joreilly/BikeShare)
*  FantasyPremierLeague (https://github.com/joreilly/FantasyPremierLeague)
*  ClimateTrace (https://github.com/joreilly/ClimateTraceKMP)
*  GeminiKMP (https://github.com/joreilly/GeminiKMP)
*  MortyComposeKMM (https://github.com/joreilly/MortyComposeKMM)
*  StarWars (https://github.com/joreilly/StarWars)
*  WordMasterKMP (https://github.com/joreilly/WordMasterKMP)
*  Chip-8 (https://github.com/joreilly/chip-8)


### Building

Open `PeopleInSpaceSwiftUI' for  iOS projects.  

To run backend you can either run `./gradlew :backend:run` or run `Server.kt` directly from Android Studio. After doing that you should then for example be able to open `http://localhost:9090/astros_local.json` in a browser.



### Compose for Web client (Wasm)

Similarly for Kotlin/Wasm based version
`./gradlew :compose-web:wasmBrowserDevelopmentRun
`

### Compose for Desktop client

This client is available in `compose-desktop` module and can be run using `./gradlew :compose-desktop:run`.  


### Backend code

Have tested this out in Google App Engine deployment.  Using shadowJar plugin to create an "uber" jar and then deploying it as shown below.  Should be possible to deploy this jar to other services as well.

```
./gradlew :backend:shadowJar
gcloud app deploy backend/build/libs/backend-all.jar --appyaml=backend/src/jvmMain/appengine/app.yaml
```


### Screenshots 

**iOS (SwiftUI + CMP)**
<br/>
<img width="738" height="502" alt="Screenshot 2025-08-10 at 22 15 21" src="https://github.com/user-attachments/assets/2befb5b7-7308-409b-b83e-cc510fe72e41" />




**Android (Jetpack Compose + CMP)**
<br/>

<img width="719" height="496" alt="Screenshot 2025-08-10 at 22 15 29" src="https://github.com/user-attachments/assets/4d97e95c-be6e-4913-a1a1-369bd7af5d7f" />



**Wear OS (Wear Compose)**
<br/>
<img width="250" alt="Wear Compose Screenshot 1" src="https://user-images.githubusercontent.com/6302/137623548-ac51ca72-572e-4009-8b34-315defdf93a5.png">
<img width="250" alt="Wear Compose Screenshot 2" src="https://user-images.githubusercontent.com/6302/137640396-851489bb-e41d-47ef-badb-e2d22454eee4.png">
<img width="250" alt="Wear Compose Screenshot 3" src="https://user-images.githubusercontent.com/6302/139468900-16ad4e95-41dc-427f-977c-b893b1751c78.png">


**Compose for Desktop**
<br/>
<img width="1134" height="881" alt="Screenshot 2025-08-10 at 22 51 15" src="https://github.com/user-attachments/assets/a1daab34-78b4-46b0-adbd-7841881843ed" />



**Compose for Web (Wasm based)**
<br/>
<img width="1352" height="1010" alt="Screenshot 2025-08-10 at 22 58 45" src="https://github.com/user-attachments/assets/9fcf9fe8-7705-4268-aa2f-ec279ed2f2d9" />



**MCP**

The `mcp-server` module uses the [Kotlin MCP SDK](https://github.com/modelcontextprotocol/kotlin-sdk) to expose an MCP tools endpoint (returning list of people in space) that
can for example be plugged in to Claude Desktop as shown below.  That module uses same KMP shared code (that uses for example Ktor, SQLDelight and Koin)

![Gry_C3FXkAAxVvN](https://github.com/user-attachments/assets/74c210b9-9a0a-4de8-8845-81380f11e4a5)


To integrate the MCP server with Claude Desktop you need to firstly run gradle `shadowJar` task and then select "Edit Config" under Developer Settings and add something 
like the following (update with your path)

```
{
  "mcpServers": {
    "kotlin-peopleinspace": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/john.oreilly/github/PeopleInSpace/mcp-server/build/libs/serverAll.jar",
        "--stdio"
      ]
    }
  }
}
```


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
* [SKIE](https://skie.touchlab.co/intro)
* [Coil](https://coil-kt.github.io/coil/)
