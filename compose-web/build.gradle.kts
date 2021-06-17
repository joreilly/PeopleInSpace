
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "0.5.0-build222"
}

version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {

    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {

        val jsMain by getting {
            dependencies {
                implementation(compose.web.widgets)
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(project(":common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = ""
    }
}

