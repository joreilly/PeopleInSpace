plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    with(Deps.Kotlinx) {
        implementation(htmlJs)
    }

    with(Deps.React) {
        implementation(styled)
        implementation(react)
        implementation(dom)
        implementation(routerDom)
    }

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
