buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://plugins.gradle.org/m2/")) // For kotlinter-gradle
    }

    dependencies {
        // keeping this here to allow AS to automatically update
        classpath("com.android.tools.build:gradle:7.0.3")

        with(Deps.Gradle) {
            classpath(kotlin)
            classpath(kotlinSerialization)
            classpath(sqlDelight)
            classpath(shadow)
            classpath(kotlinter)
            classpath(gradleVersionsPlugin)
            classpath(kmpNativeCoroutines)
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
    }
}