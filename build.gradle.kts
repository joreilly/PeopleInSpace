buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // For kotlinter-gradle
        maven(uri("https://plugins.gradle.org/m2/"))
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")
        classpath("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:${Versions.shadow}")
        classpath("org.jmailen.gradle:kotlinter-gradle:${Versions.kotlinterGradle}")
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
