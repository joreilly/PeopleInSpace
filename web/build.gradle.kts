plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.3")

    implementation("org.jetbrains:kotlin-react:17.0.1-pre.146-kotlin-1.4.30")
    implementation("org.jetbrains:kotlin-react-dom:17.0.2-pre.156-kotlin-1.5.0")
    implementation("org.jetbrains:kotlin-react-router-dom:5.1.2-pre.110-kotlin-1.4.0")
    implementation("org.jetbrains:kotlin-styled:5.2.1-pre.146-kotlin-1.4.30")

    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))

    // Material Design Components for React
    implementation(npm("@material-ui/core", "4.11.1"))

    // ReactJS Maps
    implementation(npm("pigeon-maps", "0.19.6"))

    implementation(project(":common"))
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}

afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackCli.version = "4.10.0"
    }
}
