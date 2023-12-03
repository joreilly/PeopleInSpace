plugins {
    kotlin("multiplatform")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}


kotlin {
    jvm() {
        withJava()
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            implementation("io.ktor:ktor-server-core:2.3.6")
            implementation("io.ktor:ktor-server-netty:2.3.6")
            implementation("io.ktor:ktor-server-cors:2.3.6")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
            implementation("io.ktor:ktor-server-content-negotiation:2.3.6")

            implementation("ch.qos.logback:logback-classic:1.4.14")

            implementation(project(":common"))
        }
    }
}

application {
    mainClass.set("ServerKt")
}