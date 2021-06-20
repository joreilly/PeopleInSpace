plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation(Deps.Kotlinx.htmlJs)
    implementation(Deps.Kotlin.styled)
    implementation(Deps.React.react)
    implementation(Deps.React.dom)
    implementation(Deps.React.routerDom)
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