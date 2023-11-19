plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.ben-manes.versions")
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = AndroidSdk.compile

    defaultConfig {
        applicationId = "com.surrus.peopleinspace"
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    namespace = "com.surrus.peopleinspace"

    testOptions {
        managedDevices {
            devices {
                create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel5api32") {
                    device = "Pixel 5"
                    apiLevel = 32
                    systemImageSource = "google"
                }
            }
        }
    }
}

dependencies {
    implementation(libs.osmdroidAndroid)

    with(Deps.AndroidX) {
        implementation(lifecycleRuntimeCompose)
        implementation(lifecycleRuntimeKtx)
        implementation(lifecycleViewmodelKtx)
    }

    implementation(libs.androidx.activity.compose)
    implementation(libs.splash.screen)


    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)

    implementation(libs.coilCompose)
    implementation(libs.glance.appwidget)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.test)
    implementation(libs.koin.test.junit4)

    with(Deps.Test) {
        androidTestImplementation(androidXTestJUnit)
        testImplementation(testCore)
        testImplementation(mockito)
    }

    testImplementation(libs.robolectric)
    testImplementation(libs.junit)

    // Compose testing dependencies
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    androidTestImplementation(libs.androidx.navigation.compose.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation(project(":common"))
}

