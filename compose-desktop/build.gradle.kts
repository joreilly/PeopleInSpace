plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    application
}

group = "me.joreilly"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.coil3.compose)
    implementation(libs.coil3.network.ktor)

    implementation(projects.common)
}

application {
    mainClass.set("MainKt")
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
