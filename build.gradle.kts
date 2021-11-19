buildscript {
    val kotlinVersion: String by project
    println(kotlinVersion)

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://plugins.gradle.org/m2/")) // For kotlinter-gradle
    }

    dependencies {
        // keeping this here to allow AS to automatically update
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlinVersion}")

        with(Deps.Gradle) {
            classpath(sqlDelight)
            classpath(shadow)
            classpath(kotlinter)
            classpath(gradleVersionsPlugin)
            val kmpNativeCoroutinesVersion = if (kotlinVersion == "1.6.0") "0.9.0-new-mm" else "0.8.0"
            classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:$kmpNativeCoroutinesVersion")
        }
    }
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven(url = "https://jitpack.io")
        maven(url = "https://androidx.dev/snapshots/builds/7888785/artifacts/repository")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}