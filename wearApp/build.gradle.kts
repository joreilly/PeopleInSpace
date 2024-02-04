plugins {
    id("com.android.application")
    id("kotlin-android")
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.surrus.peopleinspace"
        minSdk = libs.versions.minWearSdk.get().toInt()
        targetSdk = libs.versions.targetWearSdk.get().toInt()
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
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // temporary hack for local testing of release builds
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "benchmark-rules.pro")
            // temporary hack for local testing of release builds
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.addAll(listOf("release", "debug"))
        }
    }

    namespace = "com.surrus.peopleinspace"
}

dependencies {
    implementation(libs.osmdroidAndroid)
    implementation(libs.osm.android.compose)

    implementation(libs.androidx.activity.compose)
    implementation(libs.metrics)

    implementation(libs.wear.compose.founndation)
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.navigation)

    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.horologist.compose.layout)
    implementation(libs.glance.tiles)
    implementation(libs.coilCompose)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.splash.screen)

    implementation(libs.androidx.ui.tooling)
    implementation(libs.wear.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)

    implementation(libs.okhttp)
    implementation(libs.loggingInterceptor)

    testImplementation(libs.robolectric)
    testImplementation(libs.junit)

    // Compose testing dependencies
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.tracing)

    implementation(project(":common"))
}