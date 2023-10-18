import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("org.jetbrains.compose") version "1.5.10-dev-wasm01"
}

group = "com.example"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    wasmJs {
        moduleName = "PeopleInSpace"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
//                    open = mapOf(
//                        "app" to mapOf(
//                            "name" to "google chrome canary",
//                            "arguments" to listOf("--js-flags=--experimental-wasm-gc ")
//                        )
//                    ),
                    static = (devServer?.static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/common/")
                        add(project.rootDir.path + "/compose-web-wasm/")
                    },
                    )
            }
        }
        binaries.executable()
        //applyBinaryen()
    }
    sourceSets {
        //targetHierarchy.default()

        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2-wasm1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0-wasm0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0-wasm0")
                implementation("io.ktor:ktor-client-core:3.0.0-wasm0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm0")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm0")
            }
        }
    }
}


compose.experimental {
    web.application {}
}

compose {
    kotlinCompilerPlugin.set("1.5.2.1-rc01")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.20-RC")
}