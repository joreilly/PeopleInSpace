buildscript {
    val kotlinVersion: String by project
    println(kotlinVersion)

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://plugins.gradle.org/m2/")) // For kotlinter-gradle
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        // keeping this here to allow AS to automatically update
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlinVersion}")

        with(Deps.Gradle) {
            classpath(sqlDelight)
            classpath(shadow)
            //classpath(kotlinter)
            //classpath(gradleVersionsPlugin)
            classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.0-dev-4392-1.0.10-SNAPSHOT")
            classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:${Versions.kmpNativeCoroutinesVersion}")
        }
    }
}

allprojects {
    //apply(plugin = "org.jmailen.kotlinter")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven(url = "https://jitpack.io")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")

        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}


// On Apple Silicon we need Node.js 16.0.0
// https://youtrack.jetbrains.com/issue/KT-49109
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class) {
    rootProject.the(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class).nodeVersion = "16.0.0"
}


allprojects {
    configurations.all {
        val conf = this
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                using(module("androidx.compose.compiler:compiler:${Versions.composeCompiler}"))
            }
        }
    }
}



