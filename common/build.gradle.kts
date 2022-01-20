import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.apollographql.apollo3")
    id("com.rickclephas.kmp.nativecoroutines")
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

kotlin {
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("iOS") {}

    val sdkName: String? = System.getenv("SDK_NAME")
    val isWatchOSDevice = sdkName.orEmpty().startsWith("watchos")
    if (isWatchOSDevice) {
        watchosArm64("watch")
    } else {
        watchosX64("watch")
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

            with(Deps.Kotlinx) {
                implementation(coroutinesCore)
                implementation(serializationCore)
            }

            with(Deps.Apollo) {
                implementation(apolloRuntime)
                implementation(apolloNormalizedCacheInMemory)
            }

            with(Deps.Koin) {
                api(core)
                api(test)
            }

            with(Deps.Log) {
                api(kermit)
            }
        }
        sourceSets["commonTest"].dependencies {
            implementation(Deps.Koin.test)
            implementation(Deps.Kotlinx.coroutinesTest)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        sourceSets["androidMain"].dependencies {
            implementation(Deps.Apollo.apolloNormalizedCacheSqlite)
        }
        sourceSets["androidTest"].dependencies {
            implementation(Deps.Test.junit)
        }

        sourceSets["jvmMain"].dependencies {
            implementation(Deps.Log.slf4j)
            implementation(Deps.Apollo.apolloNormalizedCacheSqlite)
        }

        sourceSets["iOSMain"].dependencies {
            implementation(Deps.Apollo.apolloNormalizedCacheSqlite)
        }
        sourceSets["iOSTest"].dependencies {
        }

        sourceSets["watchMain"].dependencies {
        }

        sourceSets["macOSMain"].dependencies {
            implementation(Deps.Apollo.apolloNormalizedCacheSqlite)
        }

        sourceSets["jsMain"].dependencies {
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

apollo {
    packageName.set("com.surrus.common")
    codegenModels.set("operationBased")
    generateSchema.set(true)
    generateTestBuilders.set(true)
}


multiplatformSwiftPackage {
    packageName("PeopleInSpace")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}

