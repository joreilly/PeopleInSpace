plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Versions.androidCompileSdk

    defaultConfig {
        applicationId = "com.surrus.peopleinspace"
        minSdk = 26
        targetSdk = Versions.androidTargetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // temporary hack for local testing of release builds
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/licenses/**")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(libs.org.osmdroid.osmdroid.android)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.compose.compiler)
    implementation(libs.androidx.wear.compose.compose.foundation)
    implementation(libs.androidx.wear.compose.compose.material)
    implementation(libs.androidx.wear.compose.compose.navigation)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.androidx.compose.ui.ui.tooling)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.io.insert.koin.koin.androidx.compose)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)
    implementation(libs.androidx.glance.glance.wear.tiles)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.org.robolectric)
    testImplementation(libs.org.mockito.mockito.inline)
    androidTestImplementation(libs.androidx.compose.ui.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.manifest)

    implementation(projects.common)
}