plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
    implementation(npm("text-encoding"))
    implementation(npm("abort-controller"))
    implementation(npm("bufferutil"))
    implementation(npm("utf-8-validate"))
    implementation(npm("fs"))

    //React, React DOM + Wrappers (chapter 3)
    implementation("org.jetbrains:kotlin-react:16.13.0-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.93-kotlin-1.3.70")
    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))


    implementation(project(":common"))
}


kotlin {
    target {
        useCommonJs()
        browser {
            // https://kotlinlang.org/docs/reference/javascript-dce.html#known-issue-dce-and-ktor
            dceTask {
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
        }
    }
}