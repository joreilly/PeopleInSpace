// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha03")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath(kotlin("serialization", version = Versions.kotlin))
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")
        //classpath("com.juliozynger.floorplan:floorplan-gradle-plugin:${Versions.floorPlan}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven( "https://dl.bintray.com/ekito/koin")
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    }
}


