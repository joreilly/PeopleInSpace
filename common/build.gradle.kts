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
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.skie)
    id("io.github.luca992.multiplatform-swiftpackage") version "2.3.0"
}

kotlin {
    jvmToolchain(17)

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }

    mingwX64 {
        binaries {
            sharedLib(listOf(DEBUG, RELEASE)) {
                baseName = "peopleinspace"
                if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
                    // Windows CI places the static MinGW SQLite archive here so the
                    // packaged DLL has no extra SQLite runtime dependency.
                    linkerOpts("-L${layout.buildDirectory.dir("mingw-sqlite").get().asFile.invariantSeparatorsPath}", "-lssp")
                }
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
                // com.android.kotlin.multiplatform.library creates its own target type, which
                // withAndroidTarget() (the legacy KotlinAndroidTarget) never matches, so androidMain
                // would silently sit outside this group and lose the shared sources.
                withCompilations { it.target.name == "android" }
                withJvm()
                withWasmJs()
                group("apple") {
                    withIos()
                }
            }
        }
    }

    sourceSets {
        // Compose and the AndroidX ViewModels have no MinGW artifacts, so everything that needs
        // them lives here rather than in commonMain.
        val nonWindowsMain by getting {
            dependencies {
                api(libs.koin.core.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.kmp)

                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(libs.androidx.lifecycle.compose.kmp)
            }
        }

        val nonWindowsTest by getting {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        commonMain.dependencies {
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.coroutines)
            api(libs.kotlinx.serialization)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            api(libs.koin.core)
            api(libs.koin.annotations)
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

            implementation(libs.osmdroidAndroid)
            implementation(libs.osm.android.compose)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.slf4j)
            implementation(libs.kotlinx.coroutines.swing)
        }

        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
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

nuget {
    publish {
        packageId = "PeopleInSpace.Kotlin"
        version = "0.1.0"
        authors = "PeopleInSpace"
        description = "PeopleInSpace Kotlin Multiplatform library for Windows"
        rootPackage = "dev.johnoreilly.common.windows"
        include(
            "dev.johnoreilly.common.windows",
            "dev.johnoreilly.common.viewmodel",
            "dev.johnoreilly.common.remote",
        )
        // Every packNuget mints a new package version and pins it in build/PeopleInSpace.KotlinVersions.props,
        // so the .NET restore never serves a stale build (see windows/Directory.Build.props).
        snapshot = true
    }
}

// Two compiler plugins must stay off the MinGW compilations:
// - Compose: the Compose compiler refuses to run without the Compose runtime on the classpath,
//   and Compose has no MinGW artifacts. Nothing in mingwX64Main is composable.
// - Koin: Kotlin/Native's C adapter generation crashes on the IR the Koin plugin generates
//   (NullPointerException in CAdapterCodegen.buildCAdapter, via a null klib module origin;
//   KT-62984). The exported PeopleInSpaceClient owns its own dependencies and does not use Koin.
// Every other target keeps both.
configurations
    .matching { configuration ->
        configuration.name.startsWith("kotlinCompilerPluginClasspath") &&
            configuration.name.contains("MingwX64")
    }
    .configureEach {
        exclude(group = "io.insert-koin")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compose-compiler-plugin-embeddable")
    }

// The MinGW NativeSqliteDriver needs a target SQLite archive. Windows CI provisions it and links
// the final DLL; other hosts still configure this project, so they skip the MinGW link tasks.
if (!System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    tasks.matching { task ->
        task.name.startsWith("link") && task.name.endsWith("MingwX64")
    }.configureEach {
        enabled = false
    }
} else {
    // MSYS2 builds SQLite with stack protection, but the Kotlin/Native MinGW toolchain does not
    // link libssp on its own. Stage the toolchain's libssp.a next to libsqlite3.a so -lssp resolves.
    tasks.matching { task ->
        task.name.startsWith("link") && task.name.endsWith("MingwX64")
    }.configureEach {
        val sqliteDir = layout.buildDirectory.dir("mingw-sqlite").get().asFile
        val konanDataDir = System.getenv("KONAN_DATA_DIR") ?: "${System.getProperty("user.home")}/.konan"
        doFirst {
            val libssp = File(konanDataDir, "dependencies")
                .listFiles { file -> file.isDirectory && file.name.startsWith("msys2-mingw-w64-x86_64") }
                .orEmpty()
                .flatMap { toolchain -> toolchain.resolve("lib/gcc/x86_64-w64-mingw32").listFiles().orEmpty().toList() }
                .map { gcc -> gcc.resolve("libssp.a") }
                .firstOrNull { it.isFile }
                ?: error("libssp.a was not found under $konanDataDir/dependencies")
            libssp.copyTo(sqliteDir.resolve("libssp.a"), overwrite = true)
        }
    }
}
