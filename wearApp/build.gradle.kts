plugins {
    id("com.android.application")
    id("kotlin-android")
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = AndroidSdk.compile

    defaultConfig {
        applicationId = "com.surrus.peopleinspace"
        minSdk = 26
        targetSdk = AndroidSdk.target
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

    with(Deps.Android) {
        implementation(osmdroidAndroid)
    }

    with(Deps.AndroidX) {
        implementation(activityCompose)
        implementation(metrics)
    }

    with(Deps.Compose) {
        implementation(wearFoundation)
        implementation(wearMaterial)
        implementation(wearNavigation)
        implementation(coilCompose)
    }
    implementation(libs.androidx.compose.ui.tooling)

    with(Deps.Koin) {
        implementation(core)
        implementation(android)
        implementation(compose)
    }

    with (Deps.Ok) {
        implementation(okhttp)
        implementation(loggingInterceptor)
    }

    with(Deps.Test) {
        testImplementation(junit)
        androidTestImplementation(androidXTestJUnit)
        testImplementation(testCore)
        testImplementation(robolectric)
        testImplementation(mockito)

    }

    // Compose testing dependencies
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation("androidx.tracing:tracing:1.1.0")

    with(Deps.Glance) {
        implementation(tiles)
    }

    with(Deps.Horologist) {
        implementation(composeLayout)
    }

    implementation(project(":common"))
}