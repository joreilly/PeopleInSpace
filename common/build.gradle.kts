plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
        }
    }
}

kotlin {
    targets {
        val sdkName: String? = System.getenv("SDK_NAME")

        val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
        if (isiOSDevice) {
            iosArm64("iOS64")
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
    }

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
    }

    js {
        browser {
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}")

                // Ktor
                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("com.squareup.sqldelight:runtime:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}")

            }
        }

        val androidMain by getting {
            dependencies {
                // Kotlin
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

                // Ktor
                implementation("io.ktor:ktor-client-android:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-jvm:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("com.squareup.sqldelight:android-driver:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}")
            }
        }

        val jvmMain by getting {
            dependencies {
                // Kotlin
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")

                // Ktor
                implementation("io.ktor:ktor-server-core:${Versions.ktor}")

                implementation("io.ktor:ktor-client-core-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-jvm:${Versions.ktor}")
                implementation("io.ktor:ktor-client-apache:${Versions.ktor}")


                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("org.xerial:sqlite-jdbc:${Versions.sqliteJdbcDriver}")
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")

            }
        }

        val iOSMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}")

                // Ktor
                implementation("io.ktor:ktor-client-ios:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-native:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("com.squareup.sqldelight:native-driver:${Versions.sqlDelight}")

            }
        }

        val watchMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}")

                // Ktor
                implementation("io.ktor:ktor-client-ios:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-native:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-native:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("com.squareup.sqldelight:native-driver:${Versions.sqlDelight}")
            }
        }

        val macOSMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-macosx64:${Versions.kotlinCoroutines}")


                // Ktor
                implementation("io.ktor:ktor-client-curl:${Versions.ktor}")
                implementation("io.ktor:ktor-client-core-macosx64:${Versions.ktor}")
                implementation("io.ktor:ktor-client-json-macosx64:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-macosx64:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-macosx64:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-macosx64:${Versions.kotlinxSerialization}")

                // SQL Delight
                implementation("com.squareup.sqldelight:native-driver-macosx64:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:runtime-macosx64:${Versions.sqlDelight}")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}")

                // ktor
                implementation("io.ktor:ktor-client-js:${Versions.ktor}") //include http&websockets
                implementation("io.ktor:ktor-client-json-js:${Versions.ktor}")
                implementation("io.ktor:ktor-client-logging-js:${Versions.ktor}")
                implementation("io.ktor:ktor-client-serialization-js:${Versions.ktor}")

                // Serialize
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinxSerialization}")

                // SQL Delight
                //implementation("com.squareup.sqldelight:sqljs-driver:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:runtime-js:${Versions.sqlDelight}")
            }
        }
    }
}

sqldelight {
    database("PeopleInSpaceDatabase") {
        packageName = "com.surrus.peopleinspace.db"
        sourceFolders = listOf("sqldelight")
    }
}

