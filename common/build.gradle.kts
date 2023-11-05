@file:Suppress("OPT_IN_USAGE")

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("app.cash.sqldelight")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.0"
}

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.surrus.common"
}

kotlin {
    jvmToolchain(17)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosSimulatorArm64(),
        macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }

    androidTarget()
    jvm()

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            api(libs.koin.core)
            implementation(libs.koin.test)

            api(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.slf4j)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

sqldelight {
    databases {
        create("PeopleInSpaceDatabase") {
            packageName.set("com.surrus.peopleinspace.db")
        }
    }
}

multiplatformSwiftPackage {
    packageName("PeopleInSpaceKit")
    swiftToolsVersion("5.9")
    targetPlatforms {
        iOS { v("14") }
        macOS { v("12")}
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

