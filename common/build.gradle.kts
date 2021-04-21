import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("realm-kotlin")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

android {
    compileSdkVersion(AndroidSdk.compile)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

//    val isWatchOSDevice = sdkName.orEmpty().startsWith("watchos")
//    if (isWatchOSDevice) {
//        watchosArm64("watch")
//    } else {
//        watchosX86("watch")
//    }

    macosX64("macOS")
    android()
    //jvm()

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
    }

//    js {
//        browser {
//        }
//    }

    sourceSets {

        sourceSets["commonMain"].dependencies {
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}") {
                isForce = true
            }

            // Ktor
            implementation(Ktor.clientCore)
            implementation(Ktor.clientJson)
            implementation(Ktor.clientLogging)
            implementation(Ktor.clientSerialization)

            // Kotlinx Serialization
            implementation(Serialization.core)

            // Realm
            implementation(Deps.realm)

            // koin
            api(Koin.core)
            api(Koin.test)

            // kermit
            api(Deps.kermit)
        }
        sourceSets["commonTest"].dependencies {
        }

        sourceSets["androidMain"].dependencies {
            implementation(Ktor.clientAndroid)
        }
        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test-junit"))
            implementation(Test.junit)
        }

//        sourceSets["jvmMain"].dependencies {
//            implementation(Ktor.clientApache)
//            implementation(Ktor.slf4j)
//        }

        sourceSets["iOSMain"].dependencies {
            implementation(Ktor.clientIos)
        }
        sourceSets["iOSTest"].dependencies {
        }

//        sourceSets["watchMain"].dependencies {
//            implementation(Ktor.clientIos)
//        }

        sourceSets["macOSMain"].dependencies {
            implementation(Ktor.clientCio)
        }

//        sourceSets["jsMain"].dependencies {
//            implementation(Ktor.clientJs)
//        }
    }
}


multiplatformSwiftPackage {
    packageName("PeopleInSpace")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}

