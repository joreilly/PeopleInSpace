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


// JetBrains' org.jetbrains.androidx.lifecycle 2.11 desktop artifacts are empty redirects to Google's
// androidx.lifecycle ones and share their jar names, so lib/ would hold each name twice and
// distZip/distTar fail. Leave the empty redirects out; Google's jars carry the classes.
distributions {
    main {
        contents {
            exclude { it.file.path.contains("${File.separator}org.jetbrains.androidx.lifecycle${File.separator}") }
        }
    }
}
