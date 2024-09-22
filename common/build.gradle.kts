plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.skie)
    id("io.github.luca992.multiplatform-swiftpackage") version "2.2.3"
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "com.surrus.common"
}

kotlin {
    jvmToolchain(17)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
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
            api(libs.kotlinx.serialization)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            api(libs.koin.core)
            implementation(libs.koin.compose.multiplatform)
            implementation(libs.koin.test)

            api(libs.kermit)

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.androidx.lifecycle.compose.kmp)
            implementation(libs.androidx.lifecycle.viewmodel.kmp)
        }

        commonTest.dependencies {
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)

            implementation(libs.osmdroidAndroid)
            implementation(libs.osm.android.compose)
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
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

skie {
    features {
        enableSwiftUIObservingPreview = true
    }
}