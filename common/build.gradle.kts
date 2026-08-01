@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlin.native.nuget)
}

kotlin {
    jvmToolchain(17)

    iosArm64()
    iosSimulatorArm64()

    mingwX64 {
        binaries {
            sharedLib(listOf(DEBUG, RELEASE)) {
                baseName = "peopleinspace"
                if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
                    // Windows CI places the static MinGW SQLite archive here so the
                    // packaged DLL has no extra SQLite runtime dependency.
                    linkerOpts("-L${layout.buildDirectory.dir("mingw-sqlite").get().asFile.invariantSeparatorsPath}")
                }
            }
        }
    }

    macosArm64 {
        binaries {
            sharedLib(listOf(DEBUG, RELEASE)) {
                baseName = "peopleinspace"
            }
        }
    }

    android {
        namespace = "dev.johnoreilly.common"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        androidResources { enable = true }
    }
    jvm()

    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "peopleinspaceShared.js"
            }
        }
    }

    applyDefaultHierarchyTemplate {
        common {
            group("nonWindows") {
                withAndroidTarget()
                withJvm()
                withWasmJs()
                group("apple") {
                    withIos()
                    withMacos()
                }
            }
        }
    }

    sourceSets {
        val nonWindowsMain by getting {
            dependencies {
                api(libs.koin.core.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.kmp)
            }
        }

        commonMain.dependencies {
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.coroutines)
            api(libs.kotlinx.serialization)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            api(libs.koin.core)
            api(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.slf4j)
            implementation(libs.kotlinx.coroutines.swing)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        mingwX64Main.dependencies {
            implementation(libs.ktor.client.winhttp)
            implementation(libs.sqldelight.native.driver)
        }

        wasmJsMain.dependencies {
            implementation(libs.sqldelight.web.driver)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", libs.versions.sqlJs.get()))
            implementation(devNpm("copy-webpack-plugin", libs.versions.webPackPlugin.get()))
        }
    }
}

sqldelight {
    databases {
        create("PeopleInSpaceDatabase") {
            generateAsync = true
            packageName.set("dev.johnoreilly.peopleinspace.db")
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

nuget {
    publish {
        packageId = "PeopleInSpace.Kotlin"
        version = "0.1.0"
        authors = "xxfast"
        description = "PeopleInSpace Kotlin Multiplatform library for Windows"
        rootPackage = "dev.johnoreilly.common.windows"
        include("dev.johnoreilly.common.windows")
    }
}

// The MinGW NativeSqliteDriver needs a target SQLite archive. Windows CI
// provisions it and links the final DLL; macOS packs the host-native dylib.
if (!System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    tasks.matching { task ->
        task.name.startsWith("link") && task.name.endsWith("MingwX64")
    }.configureEach {
        enabled = false
    }
}
