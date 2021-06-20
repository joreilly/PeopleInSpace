object Versions {
    const val androidMinSdk = 21
    const val androidCompileSdk = 29
    const val androidTargetSdk = androidCompileSdk

    const val kotlin = "1.5.10"
    const val gradle = "7.1.0-alpha02"

    const val kotlinCoroutines = "1.5.0-native-mt"
    const val koin = "3.1.0"
    const val ktor = "1.6.0"
    const val kotlinxSerialization = "1.2.1"
    const val kotlinxHtmlJs = "0.7.3"

    const val compose = "1.0.0-beta09"
    const val navCompose = "2.4.0-alpha03"
    const val accompanist = "0.12.0"

    const val junit = "4.13.2"
    const val mockito = "3.7.7"
    const val testCore = "1.3.0"
    const val testRunner = "1.3.0"
    const val robolectric = "4.4"

    const val sqlDelight = "1.5.0"
    const val shadow = "7.0.0"
    const val kotlinterGradle = "3.4.5"

    const val material = "1.3.0"
    const val activityCompose = "1.3.0-beta2"
    const val lifecycleKtx = "2.4.0-alpha01"
    const val lifecycleRuntimeKtx = lifecycleKtx
    const val lifecycleViewmodelKtx = lifecycleKtx
    const val osmdroidAndroid = "6.1.10"

    const val kotlinReact = "17.0.1-pre.146-kotlin-1.4.30"
    const val kotlinReactDom = "17.0.1-pre.146-kotlin-1.4.30"
    const val kotlinReactRouterDom = "5.1.2-pre.110-kotlin-1.4.0"
    const val kotlinStyled = "5.2.1-pre.146-kotlin-1.4.30"

    const val slf4j = "1.7.30"
    const val logback = "1.2.3"
    const val kermit = "0.1.9"
}

object Deps {
    object Gradle {
        const val androidGradle = "com.android.tools.build:gradle:${Versions.gradle}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        const val kotlinter = "org.jmailen.gradle:kotlinter-gradle:${Versions.kotlinterGradle}"
        const val shadow = "gradle.plugin.com.github.jengelman.gradle.plugins:shadow:${Versions.shadow}"
        const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
    }

    object Kotlinx {
        const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        const val htmlJs = "org.jetbrains.kotlinx:kotlinx-html-js:${Versions.kotlinxHtmlJs}"
    }

    object Android {
        const val material = "com.google.android.material:material:${Versions.material}"
        const val osmdroidAndroid = "org.osmdroid:osmdroid-android:${Versions.osmdroidAndroid}"
    }

    object AndroidX {
        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"
        const val lifecycleViewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleViewmodelKtx}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    }

    object Test {
        const val junit = "junit:junit:${Versions.junit}"
        const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val testCore = "androidx.test:core:${Versions.testCore}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    }

    object Compose {
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"
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
        const val serverCore = "io.ktor:ktor-server-core:${Versions.ktor}"
        const val serverNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-serialization:${Versions.ktor}"
        const val websockets = "io.ktor:ktor-websockets:${Versions.ktor}"
        const val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
        const val clientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
        const val clientApache = "io.ktor:ktor-client-apache:${Versions.ktor}"
        const val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
        const val clientCio = "io.ktor:ktor-client-cio:${Versions.ktor}"
        const val clientJs = "io.ktor:ktor-client-js:${Versions.ktor}"
    }

    object SqlDelight {
        const val runtime = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        const val coroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        const val androidDriver = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val nativeDriver = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
        const val nativeDriverMacos = "com.squareup.sqldelight:native-driver-macosx64:${Versions.sqlDelight}"
        const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    }

    object React {
        const val react = "org.jetbrains:kotlin-react:${Versions.kotlinReact}"
        const val dom = "org.jetbrains:kotlin-react-dom:${Versions.kotlinReactDom}"
        const val routerDom = "org.jetbrains:kotlin-react-router-dom:${Versions.kotlinReactRouterDom}"
        const val styled = "org.jetbrains:kotlin-styled:${Versions.kotlinStyled}"
    }

    object Log {
        const val slf4j = "org.slf4j:slf4j-simple:${Versions.slf4j}"
        const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
        const val kermit = "co.touchlab:kermit:${Versions.kermit}"
    }
}