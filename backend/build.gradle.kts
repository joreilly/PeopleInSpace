plugins {
    id("kotlin-platform-jvm")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    with (Deps){
        implementation(Deps.Serialization.core) // JVM dependency
        implementation(kotlinCoroutinesCore)
        implementation(logback)
    }

    with(Deps.Ktor){
        implementation(serverCore)
        implementation(serverNetty)
        implementation(serialization)
        implementation(websockets)
    }

    implementation(project(":common"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ServerKt")
}