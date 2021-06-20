buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://plugins.gradle.org/m2/")) // For kotlinter-gradle
    }

    dependencies {
        classpath(Deps.Gradle.kotlin)
        classpath(Deps.Gradle.kotlinSerialization)
        classpath(Deps.Gradle.androidGradle)
        classpath(Deps.Gradle.sqlDelight)
        classpath(Deps.Gradle.shadow)
        classpath(Deps.Gradle.kotlinter)
    }
}

allprojects {
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers/")
        maven(url = "https://jitpack.io")
    }
}