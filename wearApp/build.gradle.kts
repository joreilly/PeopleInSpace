plugins {
    id("com.android.application")
    id("kotlin-android")
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    kotlinOptions {
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += "-opt-in=com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi"
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

    packagingOptions {
        resources.excludes.add("META-INF/licenses/**")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
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
        implementation(compiler)
        implementation(wearFoundation)
        implementation(wearMaterial)
        implementation(wearNavigation)
        implementation(coilCompose)
        implementation(uiTooling)
    }

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

        // Compose testing dependencies
        androidTestImplementation(composeUiTest)
        androidTestImplementation(composeUiTestJUnit)
        debugImplementation(composeUiTestManifest)
    }

    with(Deps.Glance) {
        implementation(tiles)
    }

    with(Deps.Horologist) {
        implementation(composeLayout)
    }

    implementation(project(":common"))
}