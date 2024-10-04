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
