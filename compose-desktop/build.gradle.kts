import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
    application
}

group = "me.joreilly"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.imageLoader)
    implementation(project(":common"))
}

application {
    mainClass.set("MainKt")
}



compose {
    kotlinCompilerPlugin.set(libs.versions.jbComposeCompiler)
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.20-RC")
}
