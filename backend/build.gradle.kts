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
        val jvmMain by getting {
            dependencies {
                with(Deps.Kotlinx) {
                    implementation(serializationCore) // JVM dependency
                    implementation(coroutinesCore)
                }

                with(Deps.Ktor) {
                    implementation(serverCore)
                    implementation(serverNetty)
                    implementation(serverCors)
                    implementation(serverContentNegotiation)
                    implementation(json)
                }

                with(Deps.Log) {
                    implementation(logback)
                }

                implementation(project(":common"))
            }
        }
    }
}

application {
    mainClass.set("ServerKt")
}