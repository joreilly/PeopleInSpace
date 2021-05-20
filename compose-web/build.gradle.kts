
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "0.0.0-web-dev-12"
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

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(compose.web.web)
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

