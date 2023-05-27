import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("io.github.luca992.multiplatform-swiftpackage") version "2.1.1"
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.surrus.common"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),

        watchosArm32(),
        watchosArm64(),
        watchosSimulatorArm64()

    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }


    //macosX64("macOS")
    macosArm64("macOS") {
        binaries.framework {
            baseName = "common"
        }
    }
    android()
    jvm()

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Deps.Ktor) {
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(clientLogging)
                    implementation(contentNegotiation)
                    implementation(json)
                }

                with(Deps.Kotlinx) {
                    implementation(coroutinesCore)
                    implementation(serializationCore)
                }

                with(Deps.SqlDelight) {
                    implementation(runtime)
                    implementation(coroutineExtensions)
                }

                with(Deps.Koin) {
                    api(core)
                    api(test)
                }

                with(Deps.Log) {
                    api(kermit)
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Koin.test)
                implementation(Deps.Kotlinx.coroutinesTest)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientAndroid)
                implementation(Deps.SqlDelight.androidDriver)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientJava)
                implementation(Deps.SqlDelight.sqliteDriver)
                implementation(Deps.Log.slf4j)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Deps.Ktor.clientDarwin)
                implementation(Deps.SqlDelight.nativeDriver)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }


        val watchosArm32Main by getting
        val watchosArm64Main by getting
        val watchosSimulatorArm64Main by getting
        val watchMain by creating {
            watchosArm32Main.dependsOn(this)
            watchosArm64Main.dependsOn(this)
            watchosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Deps.Ktor.clientDarwin)
                implementation(Deps.SqlDelight.nativeDriver)
            }
        }

        val macOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientDarwin)
                implementation(Deps.SqlDelight.nativeDriverMacos)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientJs)
            }
        }

    }
}

tasks.withType<KotlinCompile> {
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
    packageName("PeopleInSpaceKit")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("14") }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}


// workaround for https://youtrack.jetbrains.com/issue/KT-55751 - should be fixed in Kotlin 1.9
val myAttribute = Attribute.of("myOwnAttribute", String::class.java)

if (configurations.findByName("podDebugFrameworkIosArm64") != null) {
    configurations.named("podDebugFrameworkIosArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "podDebugFrameworkIosArm64")
        }

    }
}

if (configurations.findByName("podDebugFrameworkIosArm64") != null) {
    configurations.named("podDebugFrameworkIosArm64").configure {
        attributes {
            attribute(myAttribute, "podDebugFrameworkIosArm64")
        }
    }
}




if (configurations.findByName("podDebugFrameworkIosSimulatorArm64") != null) {
    configurations.named("podDebugFrameworkIosSimulatorArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "podDebugFrameworkIosSimulatorArm64")
        }

    }
}

if (configurations.findByName("debugFrameworkIosSimulatorArm64") != null) {
    configurations.named("debugFrameworkIosSimulatorArm64").configure {
        attributes {
            attribute(myAttribute, "debugFrameworkIosSimulatorArm64")
        }
    }
}


if (configurations.findByName("podDebugFrameworkIosX64") != null) {
    configurations.named("podDebugFrameworkIosX64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "podDebugFrameworkIosX64")
        }

    }
}

if (configurations.findByName("debugFrameworkIosX64") != null) {
    configurations.named("debugFrameworkIosX64").configure {
        attributes {
            attribute(myAttribute, "debugFrameworkIosX64")
        }
    }
}



if (configurations.findByName("podDebugFrameworkIosFat") != null) {
    configurations.named("podDebugFrameworkIosFat").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "podDebugFrameworkIosFat")
        }

    }
}

if (configurations.findByName("podReleaseFrameworkIosFat") != null) {
    configurations.named("podReleaseFrameworkIosFat").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkIosFat")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkMacOS") != null) {
    configurations.named("podReleaseFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkMacOS")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkMacOS") != null) {
    configurations.named("podReleaseFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkMacOS")
        }
    }
}

if (configurations.findByName("podDebugFrameworkMacOS") != null) {
    configurations.named("podDebugFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "podDebugFrameworkMacOS")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkWatch") != null) {
    configurations.named("podReleaseFrameworkWatch").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkWatch")
        }
    }
}


if (configurations.findByName("podDebugFrameworkWatchosFat") != null) {
    configurations.named("podDebugFrameworkWatchosFat").configure {
        attributes {
            attribute(myAttribute, "podDebugFrameworkWatchosFat")
        }
    }
}

if (configurations.findByName("podDebugFrameworkWatch") != null) {
    configurations.named("podDebugFrameworkWatch").configure {
        attributes {
            attribute(myAttribute, "podDebugFrameworkWatch")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkIosArm64") != null) {
    configurations.named("podReleaseFrameworkIosArm64").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkIosArm64")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkIosSimulatorArm64") != null) {
    configurations.named("podReleaseFrameworkIosSimulatorArm64").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkIosSimulatorArm64")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkIosX64") != null) {
    configurations.named("podReleaseFrameworkIosX64").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkIosX64")
        }
    }
}


if (configurations.findByName("debugFrameworkMacOS") != null) {
    configurations.named("debugFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "debugFrameworkMacOS")
        }
    }
}

if (configurations.findByName("releaseFrameworkMacOS") != null) {
    configurations.named("releaseFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "releaseFrameworkMacOS")
        }
    }
}

if (configurations.findByName("watchosX64ApiElements") != null) {
    configurations.named("watchosX64ApiElements").configure {
        attributes {
            attribute(myAttribute, "watchosX64ApiElements")
        }
    }
}

if (configurations.findByName("watchosX64CInteropApiElements") != null) {
    configurations.named("watchosX64CInteropApiElements").configure {
        attributes {
            attribute(myAttribute, "watchosX64CInteropApiElements")
        }
    }
}

if (configurations.findByName("watchosX64MetadataElements") != null) {
    configurations.named("watchosX64MetadataElements").configure {
        attributes {
            attribute(myAttribute, "watchosX64MetadataElements")
        }
    }
}

if (configurations.findByName("watchosX64SourcesElements") != null) {
    configurations.named("watchosX64SourcesElements").configure {
        attributes {
            attribute(myAttribute, "watchosX64SourcesElements")
        }
    }
}

if (configurations.findByName("debugFrameworkWatchosFat") != null) {
    configurations.named("debugFrameworkWatchosFat").configure {
        attributes {
            attribute(myAttribute, "debugFrameworkWatchosFat")
        }
    }
}

if (configurations.findByName("releaseFrameworkWatchosFat") != null) {
    configurations.named("releaseFrameworkWatchosFat").configure {
        attributes {
            attribute(myAttribute, "releaseFrameworkWatchosFat")
        }
    }
}

