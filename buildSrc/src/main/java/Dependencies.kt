// Gradually migrating this to version catalogs

object Versions {
    const val androidXTestJUnit = "1.1.5"
    const val testCore = "1.5.0"
    const val mockito = "3.11.2"
    const val robolectric = "4.10"

    const val gradleVersionsPlugin = "0.39.0"
}

object AndroidSdk {
    const val min = 21
    const val compile = 34
    const val target = compile
}

object Deps {
    object AndroidX {
        const val benchmarkMacroJunit4 = "androidx.benchmark:benchmark-macro-junit4:1.2.2"
        const val benchmarkJunit4 = "androidx.benchmark:benchmark-junit4:1.2.2"
        const val testEspressoCore = "androidx.test.espresso:espresso-core:3.5.1"
        const val testExtJunit = "androidx.test.ext:junit:1.1.5"
        const val testUiautomator = "androidx.test.uiautomator:uiautomator:2.2.0"
    }

    object Test {
        const val androidXTestJUnit = "androidx.test.ext:junit:${Versions.androidXTestJUnit}"
        const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
        const val testCore = "androidx.test:core:${Versions.testCore}"
    }
}
