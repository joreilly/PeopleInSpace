@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.shadowPlugin)
}

kotlin {
    jvm() {
        binaries {
            executable {
                mainClass.set("ServerKt")
            }
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            implementation("io.ktor:ktor-server-core:3.2.3")
            implementation("io.ktor:ktor-server-netty:3.2.3")
            implementation("io.ktor:ktor-server-cors:3.2.3")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")
            implementation("io.ktor:ktor-server-content-negotiation:3.2.3")

            implementation("ch.qos.logback:logback-classic:1.5.8")

            implementation(projects.common)
        }
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    manifest {
        attributes["Main-Class"] = "ServerKt"
    }
}

