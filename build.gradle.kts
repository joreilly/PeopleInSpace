buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // For kotlinter-gradle
        maven(uri("https://plugins.gradle.org/m2/"))
    }

    dependencies {
        classpath(Deps.gradle)
        classpath(Deps.kotlin)
        classpath(Deps.kotlinSerialization)
        classpath(Deps.sqlDelight)
        classpath(Deps.shadow)
        classpath(Deps.kotlinterGradle)
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
