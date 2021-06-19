object Versions {
    const val kotlin = "1.5.10"
    const val kotlinCoroutines = "1.5.0-native-mt"
    const val ktor = "1.6.0"
    const val kotlinxSerialization = "1.2.1"
    const val koin = "3.1.0"
    const val sqlDelight = "1.5.0"
    const val kermit = "0.1.9"

    const val slf4j = "1.7.30"
    const val compose = "1.0.0-beta09"
    const val nav_compose = "2.4.0-alpha03"
    const val accompanist = "0.12.0"

    const val junit = "4.13"
    const val mockito = "3.7.7"
    const val testRunner = "1.3.0"

    const val gradle = "7.1.0-alpha02"
    const val kotlinterGradle = "3.4.5"
    const val shadow = "7.0.0"
}


object AndroidSdk {
    const val min = 21
    const val compile = 29
    const val target = compile
}

object Deps {
    const val kermit = "co.touchlab:kermit:${Versions.kermit}"
}

object Test {
    const val junit = "junit:junit:${Versions.junit}"
    const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
}

object Compose {
    const val ui = "androidx.compose.ui:ui:${Versions.compose}"
    const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
    const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
    const val material = "androidx.compose.material:material:${Versions.compose}"
    const val navigation = "androidx.navigation:navigation-compose:${Versions.nav_compose}"
    const val accompanistCoil = "com.google.accompanist:accompanist-coil:${Versions.accompanist}"
    const val accompanistPlaceholder = "com.google.accompanist:accompanist-placeholder:${Versions.accompanist}"
}

object Koin {
    const val core = "io.insert-koin:koin-core:${Versions.koin}"
    const val test = "io.insert-koin:koin-test:${Versions.koin}"
    const val testJUnit4 = "io.insert-koin:koin-test-junit4:${Versions.koin}"
    const val android = "io.insert-koin:koin-android:${Versions.koin}"
    const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
}

object Ktor {
    const val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
    const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
    const val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"

    const val clientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
    const val clientApache = "io.ktor:ktor-client-apache:${Versions.ktor}"
    const val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j}"
    const val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
    const val clientCio = "io.ktor:ktor-client-cio:${Versions.ktor}"
    const val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
}

object Serialization {
    const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
}

object SqlDelight {
    const val runtime = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
    const val coroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
    const val androidDriver = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"

    const val nativeDriver = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
    const val nativeDriverMacos = "com.squareup.sqldelight:native-driver-macosx64:${Versions.sqlDelight}"
    const val sqlliteDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
}

