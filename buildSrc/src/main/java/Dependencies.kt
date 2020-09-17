
object Versions {
    const val kotlin = "1.4.10"
    const val kotlinCoroutines = "1.3.9-native-mt"
    const val ktor = "1.4.0"
    const val kotlinxSerialization = "1.0.0-RC"
    const val koin = "3.0.0-alpha-4"
    const val sqlDelight = "1.4.2"

    const val sqliteJdbcDriver = "3.30.1"
    const val floorPlan = "0.2"
    const val slf4j = "1.7.30"
    const val ktx = "1.0.1"
    const val nav = "2.1.0-alpha04"
    const val work = "2.1.0-alpha02"
    const val room = "2.1.0-beta01"
    const val lifecycle = "2.2.0-alpha01"
    const val fragment = "1.1.0-alpha09"
    const val compose = "1.0.0-alpha03"
    const val coilVersion = "0.2.1"
    const val composeRouterVersion = "0.18.0"

    const val junit = "4.13"
    const val testRunner = "1.3.0"
}


object AndroidSdk {
    const val min = 21
    const val compile = 28
    const val target = compile
}

object Compose {
    const val ui = "androidx.compose.ui:ui:${Versions.compose}"
    const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
    const val uiTooling = "androidx.ui:ui-tooling:${Versions.compose}"
    const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
    const val material = "androidx.compose.material:material:${Versions.compose}"
    const val runtimeLiveData =  "androidx.compose.runtime:runtime-livedata:${Versions.compose}"

}

object Koin {
    val core = "org.koin:koin-core:${Versions.koin}"
    val android = "org.koin:koin-android:${Versions.koin}"
    val androidViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
}


