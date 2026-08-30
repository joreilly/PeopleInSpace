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

// Kotlin/Native's C adapter generation crashes on the IR the Koin compiler plugin generates
// (NullPointerException in CAdapterCodegen.buildCAdapter, via a null klib module origin). It only
// bites the target that links a sharedLib for the NuGet package, and that target does not use
// Koin: the exported PeopleInSpaceClient owns its own dependencies. So keep the compiler plugin off
// its compilations and let every other target keep annotation-driven DI.
configurations
    .matching { configuration ->
        configuration.name.startsWith("kotlinCompilerPluginClasspath") &&
            configuration.name.contains("MingwX64")
    }
    .configureEach { exclude(group = "io.insert-koin") }

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
