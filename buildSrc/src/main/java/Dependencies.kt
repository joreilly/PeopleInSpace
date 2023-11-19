// Gradually migrating this to version catalogs

object Versions {
    const val ktor = "2.3.5"

    const val kotlinxHtmlJs = "0.7.3"

    const val androidXTestJUnit = "1.1.5"
    const val testCore = "1.3.0"
    const val mockito = "3.11.2"
    const val robolectric = "4.10"

    const val shadow = "7.0.0"

    const val activityCompose = "1.8.0"
    const val lifecycleKtx = "2.6.1"
    const val lifecycleRuntimeKtx = lifecycleKtx
    const val lifecycleViewmodelKtx = lifecycleKtx
    const val osmdroidAndroid = "6.1.17"

    const val kotlinReact = "17.0.1-pre.146-kotlin-1.4.30"
    const val kotlinReactDom = "17.0.1-pre.146-kotlin-1.4.30"
    const val kotlinReactRouterDom = "5.1.2-pre.110-kotlin-1.4.0"
    const val kotlinStyled = "5.2.1-pre.146-kotlin-1.4.30"

    const val logback = "1.2.3"

    const val gradleVersionsPlugin = "0.39.0"
}

object AndroidSdk {
    const val min = 21
    const val compile = 34
    const val target = compile
}


object Deps {
    object Kotlinx {
        const val htmlJs = "org.jetbrains.kotlinx:kotlinx-html-js:${Versions.kotlinxHtmlJs}"
    }

    object Android {
        const val osmdroidAndroid = "org.osmdroid:osmdroid-android:${Versions.osmdroidAndroid}"
    }

    object AndroidX {
        const val benchmarkMacroJunit4 = "androidx.benchmark:benchmark-macro-junit4:1.2.0"
        const val benchmarkJunit4 = "androidx.benchmark:benchmark-junit4:1.2.0"
        const val lifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycleRuntimeKtx}"
        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"
        const val lifecycleViewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleViewmodelKtx}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
        const val metrics = "androidx.metrics:metrics-performance:1.0.0-alpha01"
        const val testEspressoCore = "androidx.test.espresso:espresso-core:3.4.0"
        const val testExtJunit = "androidx.test.ext:junit:1.1.5"
        const val testUiautomator = "androidx.test.uiautomator:uiautomator:2.2.0"

        const val splashScreen = "androidx.core:core-splashscreen:1.0.1"
    }

    object Test {
        const val androidXTestJUnit = "androidx.test.ext:junit:${Versions.androidXTestJUnit}"
        const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
        const val testCore = "androidx.test:core:${Versions.testCore}"
    }

    object Ktor {
        const val serverCore = "io.ktor:ktor-server-core:${Versions.ktor}"
        const val serverNetty = "io.ktor:ktor-server-netty:${Versions.ktor}"
        const val serverCors = "io.ktor:ktor-server-cors:${Versions.ktor}"
        const val json = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"

        const val serverContentNegotiation = "io.ktor:ktor-server-content-negotiation:${Versions.ktor}"
    }

    object React {
        const val react = "org.jetbrains:kotlin-react:${Versions.kotlinReact}"
        const val dom = "org.jetbrains:kotlin-react-dom:${Versions.kotlinReactDom}"
        const val routerDom = "org.jetbrains:kotlin-react-router-dom:${Versions.kotlinReactRouterDom}"
        const val styled = "org.jetbrains:kotlin-styled:${Versions.kotlinStyled}"
    }

    object Ok {
        const val okhttp = "com.squareup.okhttp3:okhttp:4.9.2"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.2"
    }

    object Log {
        const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
    }
}
