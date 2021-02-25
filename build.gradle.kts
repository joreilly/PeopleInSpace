plugins {
//    id("com.android.application") version "4.0.0" apply false
    id("com.google.protobuf") version "0.8.13" apply false
//    kotlin("jvm") version "1.3.72" apply false
//    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("io.vertx.vertx-plugin") version "1.2.0" apply false
}

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha08")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
//        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://dl.bintray.com/ekito/koin")
        maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers/")
        maven(url = "https://jitpack.io")
    }

//    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}


tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
//    wrapper {
//        gradleVersion = "6.0.1"
//        distributionType = Wrapper.DistributionType.ALL
//    }
}


task<Wrapper>("wrapper") {
    gradleVersion = "6.8.2"
    distributionType = Wrapper.DistributionType.ALL
}