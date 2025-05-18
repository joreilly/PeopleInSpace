@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
        moduleName = "peopleinspace"
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

//                implementation(libs.kotlinx.coroutines)
//                implementation(libs.kotlinx.serialization)
//
//                implementation(libs.ktor.client.core)
//                implementation(libs.ktor.serialization.kotlinx.json)
//                implementation(libs.ktor.client.content.negotiation)

                implementation(projects.common)
            }
        }
    }
}


compose.experimental {
    web.application {}
}
