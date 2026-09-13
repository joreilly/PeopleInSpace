@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.native.nuget)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)

    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalSwiftExportDsl::class)
    swiftExport {
        moduleName = "Common"
        flattenPackage = "dev.johnoreilly.common"
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

            implementation(projects.db)

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
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.android)

            implementation(libs.osmdroidAndroid)
            implementation(libs.osm.android.compose)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.ktor.client.java)
            implementation(libs.slf4j)
            implementation(libs.kotlinx.coroutines.swing)
        }

        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }

        appleMain.dependencies {
            implementation(libs.sqldelight.native.driver)
            implementation(libs.ktor.client.darwin)
        }

        mingwX64Main.dependencies {
            implementation(libs.sqldelight.native.driver)
            implementation(libs.ktor.client.winhttp)
        }

        wasmJsMain.dependencies {
            implementation(libs.sqldelight.web.driver)
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

// WORKAROUND (Swift Export alpha, Kotlin 2.4.20): an interface member behind a @RequiresOptIn
// marker that ALSO has a Kotlin default body is generated twice in the same unconstrained Swift
// extension -- once as an @_spi "must be implemented by Swift conformers" fatalError stub, once
// as the real bridge -- which Swift rejects as `invalid redeclaration`. Hit here via
// kotlinx-serialization-core, reachable from every @Serializable type's Companion.serializer(),
// so it cannot be removed by narrowing the exported surface. Drop the stub wherever a same-named
// twin exists in the same extension. Remove once the exporter stops emitting the pair.
val swiftExportOutputDirs = listOf(
    layout.buildDirectory.dir("SPMPackage"),
    layout.buildDirectory.dir("SwiftExport"),
).map { it.get().asFile }

tasks.matching { it.name.endsWith("GenerateSPMPackage") }.configureEach {
    val dirs = swiftExportOutputDirs
    doLast {
        var removed = 0
        dirs.flatMap { root ->
            if (root.exists()) root.walkTopDown().filter { it.name.endsWith(".swift") }.toList() else emptyList()
        }.forEach { f ->
            val lines = f.readText().split("\n")
            val extStarts = lines.indices.filter { lines[it].startsWith("extension ") }
            val blocks = extStarts.mapIndexed { i, s -> s to (extStarts.getOrNull(i + 1) ?: lines.size) }
            val drop = sortedSetOf<Int>()
            blocks.forEach { (start, end) ->
                val counts = mutableMapOf<String, Int>()
                for (i in start until end) {
                    Regex("""\s+(?:public |package |open )?func (\w+)\(""").find(lines[i])
                        ?.let { counts.merge(it.groupValues[1], 1, Int::plus) }
                }
                var i = start
                while (i < end) {
                    if (lines[i].trim().startsWith("@_spi(") && i + 1 < end) {
                        var j = i + 1
                        while (j < end && !lines[j].contains("{")) j++
                        var e = j + 1
                        while (e < end && lines[e].trim() != "}") e++
                        val block = (i..minOf(e, end - 1)).joinToString("\n") { lines[it] }
                        val name = Regex("""func (\w+)\(""").find(block)?.groupValues?.get(1)
                        if (name != null && block.contains("is an @_spi requirement that must be implemented")
                            && (counts[name] ?: 0) > 1
                        ) {
                            (i..e).forEach { drop.add(it) }
                            removed++
                            i = e + 1
                            continue
                        }
                    }
                    i++
                }
            }
            // Second defect: when a declaration uses a type the exporter cannot express (here,
            // Koin's generated `Module.module()` extensions whose receivers are internal), it
            // erases the receiver to Swift.Never and marks the stub @available(*, unavailable).
            // It does not deduplicate them, so N such declarations collide. They are unusable by
            // construction, so keep only the first of each identical signature.
            val kept = lines.filterIndexed { i, _ -> i !in drop }.toMutableList()
            val seenUnavailable = mutableSetOf<String>()
            val drop2 = sortedSetOf<Int>()
            var k = 0
            while (k < kept.size) {
                if (kept[k].trim().startsWith("@available(*, unavailable")) {
                    var e = k + 1
                    while (e < kept.size && kept[e].trim() != "}") e++
                    val sig = (k..minOf(e, kept.size - 1)).joinToString("\n") { kept[it] }
                    if (!seenUnavailable.add(sig)) {
                        (k..e).forEach { drop2.add(it) }
                        removed++
                    }
                    k = e + 1
                    continue
                }
                k++
            }
            val finalLines = kept.filterIndexed { i, _ -> i !in drop2 }
            if (drop.isNotEmpty() || drop2.isNotEmpty()) f.writeText(finalLines.joinToString("\n"))
        }
        if (removed > 0) println("Swift Export: removed $removed duplicate @_spi stub(s)")
    }
}
