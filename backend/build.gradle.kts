import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin-platform-jvm")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ServerKt")
}