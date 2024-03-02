import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
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
        //applyBinaryen()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha05")
                implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha05")

                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                implementation("io.ktor:ktor-client-core:3.0.0-wasm1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm1")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm1")
            }
        }
    }
}


compose.experimental {
    web.application {}
}
