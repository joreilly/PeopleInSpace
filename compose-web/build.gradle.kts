@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

group = "com.example"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "peopleinspace.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.coil3.compose)
                implementation(libs.coil3.network.ktor)
                implementation(projects.common)
            }
        }
        wasmJsMain {
            dependencies {
                // used by webpack.config.d/config.js; no longer hoisted into node_modules from Kotlin 2.4.20
                implementation(devNpm("terser-webpack-plugin", "5.6.1"))
            }
        }
    }
}



