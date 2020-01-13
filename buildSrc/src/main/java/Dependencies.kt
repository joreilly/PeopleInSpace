
object Versions {
    const val androidBuildToolsVersion = "4.0.0-alpha08"

    const val kotlin = "1.3.61"
    const val kotlinCoroutines = "1.3.3"
    const val koin = "2.0.0"
    const val ktor = "1.3.0-rc"
    const val kotlinxSerialization = "0.14.0"
    const val sqlDelight = "1.2.0"
    const val retrofit = "2.4.0"
    const val okHttp = "3.12.0"
    const val ktx = "1.0.1"
    const val nav = "2.1.0-alpha04"
    const val work = "2.1.0-alpha02"
    const val room = "2.1.0-beta01"
    const val lifecycle = "2.2.0-alpha01"
    const val fragment = "1.1.0-alpha09"

    const val junit = "4.12"
    const val coreTesting = "2.0.0"
    const val coroutinesTest = "1.1.0"
    const val mockito = "2.27.0"
    const val mockitoKotlin = "2.1.0"
    const val robolectric = "3.6.1"
    const val fragmentTest = "1.1.0-alpha09"
    const val screengrab = "1.2.0"
    const val mockWebServer = "3.11.0"

    const val appengine = "2.1.0"

    const val html = "0.6.9"
    const val reactRouterDom = "4.3.1"
    const val kotlinReactRouterDom = "$reactRouterDom-pre.79-kotlin-1.3.41"

    const val node = "10.13.0"
    const val react = "16.8.6"
    const val kotlinFrontendPlugin = "0.0.45"
    const val kotlinJsExt = "1.0.1-pre.79-kotlin-1.3.41"
    const val kotlinWrapper = "$react-pre.79-kotlin-1.3.41"
}


object BuildPlugins {

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidBuildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
}




object Kotlin {
    const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
    const val stdlibJs = "org.jetbrains.kotlin:kotlin-stdlib-js:${Versions.kotlin}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
    const val coroutinesCoreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}"
    const val coroutinesCoreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
    const val coroutinesCoreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"

    const val serializationRuntime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerialization}"
    const val serializationRuntimeCommon = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinxSerialization}"
    const val serializationRuntimeJs = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinxSerialization}"
    const val serializationRuntimeNative = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinxSerialization}"

    const val htmlJs = "org.jetbrains.kotlinx:kotlinx-html-js:${Versions.html}"

    const val extensions = "org.jetbrains:kotlin-extensions:${Versions.kotlinJsExt}"
    const val react = "org.jetbrains:kotlin-react:${Versions.kotlinWrapper}"
    const val reactDom = "org.jetbrains:kotlin-react-dom:${Versions.kotlinWrapper}"
    const val reactRouterDom = "org.jetbrains:kotlin-react-router-dom:${Versions.kotlinReactRouterDom}"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
}


object Ktor {
    const val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
    const val clientJsonJs = "io.ktor:ktor-client-json-js:${Versions.ktor}"
    const val clientLoggingJs = "io.ktor:ktor-client-logging-js:${Versions.ktor}"
    const val clientSerializationJs = "io.ktor:ktor-client-serialization-js:${Versions.ktor}"
    const val freemaker = "io.ktor:ktor-freemarker:${Versions.ktor}"
    const val locations = "io.ktor:ktor-locations:${Versions.ktor}"
}

object AndroidSdk {
    const val min = 21
    const val compile = 28
    const val target = compile
}

object ArchComponents {
    val coreKtx = "androidx.core:core-ktx:${Versions.ktx}"
    val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    val navFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.nav}"
    val navUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.nav}"
    val work = "androidx.work:work-runtime-ktx:${Versions.work}"
    val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    val roomCompiler =  "androidx.room:room-compiler:${Versions.room}"
    val lifecycleExtensions =  "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
}


object Koin {
    val core = "org.koin:koin-core:${Versions.koin}"
    val android = "org.koin:koin-android:${Versions.koin}"
    val androidViewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
}

object Firebase {
    val core = "com.google.firebase:firebase-core:16.0.9"
    val performance = "com.google.firebase:firebase-perf:16.2.3"
}

object PlayServices {
    val location = "com.google.android.gms:play-services-location:16.0.0"
    val maps = "com.google.android.gms:play-services-maps:16.1.0"
}


object Okhttp {
    val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
}

object Retrofit {
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val converterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val converterScalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    val coroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
}

object Testing {
    val junit = "junit:junit:${Versions.junit}"
    val coreTesting = "android.arch.core:core-testing:${Versions.coreTesting}"
    val couroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesTest}"

    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    val fragmentTesting = "androidx.fragment:fragment-testing:${Versions.fragmentTest}"
    val screengrab = "tools.fastlane:screengrab:${Versions.screengrab}"
    val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
}

object Mockito {
    val core = "org.mockito:mockito-core:${Versions.mockito}"
    val android = "org.mockito:mockito-android:${Versions.mockito}"
    val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
}

