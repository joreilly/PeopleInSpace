plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.3")

    implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-1.4.0")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-1.4.0")
    implementation("org.jetbrains:kotlin-react-router-dom:5.1.2-pre.110-kotlin-1.4.0")
    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))

    implementation(project(":common"))
}


kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}