// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidBuildToolsVersion}")
        classpath(kotlin("gradle-plugin", version = "${Versions.kotlin}"))
        classpath(kotlin("serialization", version = "${Versions.kotlin}"))
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    }
}


