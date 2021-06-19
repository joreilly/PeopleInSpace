plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.squareup.sqldelight")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

android {
    compileSdk = Versions.androidCompileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Versions.androidMinSdk
        targetSdk = Versions.androidTargetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// workaround for https://youtrack.jetbrains.com/issue/KT-43944
android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}


kotlin {
    val sdkName: String? = System.getenv("SDK_NAME")

    val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
    if (isiOSDevice) {
        iosArm64("iOS")
    } else {
        iosX64("iOS")
    }

    val isWatchOSDevice = sdkName.orEmpty().startsWith("watchos")
    if (isWatchOSDevice) {
        watchosArm64("watch")
    } else {
        watchosX86("watch")
    }

    macosX64("macOS")
    android()
    jvm()

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "PeopleInSpace"
        homepage = "https://github.com/joreilly/PeopleInSpace"
    }

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {

        sourceSets["commonMain"].dependencies {
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}") {
                isForce = true
            }

            // Ktor
            implementation(Deps.Ktor.clientCore)
            implementation(Deps.Ktor.clientJson)
            implementation(Deps.Ktor.clientLogging)
            implementation(Deps.Ktor.clientSerialization)

            // Kotlinx Serialization
            implementation(Deps.Serialization.core)

            // SQL Delight
            implementation(Deps.SqlDelight.runtime)
            implementation(Deps.SqlDelight.coroutineExtensions)

            // koin
            api(Deps.Koin.core)
            api(Deps.Koin.test)

            // kermit
            api(Deps.kermit)
        }
        sourceSets["commonTest"].dependencies {
        }

        sourceSets["androidMain"].dependencies {
            implementation(Deps.Ktor.clientAndroid)
            implementation(Deps.SqlDelight.androidDriver)
        }
        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test-junit"))
            implementation(Deps.Test.junit)
        }

        sourceSets["jvmMain"].dependencies {
            implementation(Deps.Ktor.clientApache)
            implementation(Deps.Ktor.slf4j)
            implementation(Deps.SqlDelight.sqliteDriver)
        }

        sourceSets["iOSMain"].dependencies {
            implementation(Deps.Ktor.clientIos)
            implementation(Deps.SqlDelight.nativeDriver)
        }
        sourceSets["iOSTest"].dependencies {
        }

        sourceSets["watchMain"].dependencies {
            implementation(Deps.Ktor.clientIos)
            implementation(Deps.SqlDelight.nativeDriver)
        }

        sourceSets["macOSMain"].dependencies {
            implementation(Deps.Ktor.clientCio)
            implementation(Deps.SqlDelight.nativeDriverMacos)
        }

        sourceSets["jsMain"].dependencies {
            implementation(Deps.Ktor.clientJs)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

sqldelight {
    database("PeopleInSpaceDatabase") {
        packageName = "com.surrus.peopleinspace.db"
        sourceFolders = listOf("sqldelight")
    }
}

multiplatformSwiftPackage {
    packageName("PeopleInSpace")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}

