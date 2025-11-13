plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadowPlugin)
    application
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:3.2.3"))
    implementation(libs.mcp.kotlin)
    implementation(projects.common)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.java)
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-cio")
    implementation("io.ktor:ktor-server-sse")
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

