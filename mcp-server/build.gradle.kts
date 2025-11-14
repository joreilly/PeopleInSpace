plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadowPlugin)
    application
}

dependencies {
    implementation(libs.ktor.client.java)
    implementation(libs.mcp.kotlin)
    implementation(libs.koin.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.sse)
    implementation(projects.common)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "MainKt"
}

tasks.shadowJar {
    archiveFileName.set("serverAll.jar")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

