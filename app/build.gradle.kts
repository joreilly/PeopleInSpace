plugins {
    id("com.android.application")
    id("kotlinx-serialization")
    id("com.github.ben-manes.versions")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
}

android {

    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "dev.johnoreilly.peopleinspace"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

    namespace = "dev.johnoreilly.peopleinspace"

    // Compose UI tests run on the JVM with Robolectric, which needs the merged resources
    testOptions {
        unitTests.isIncludeAndroidResources = true
        // Robolectric touches java.io.FileDescriptor internals, which newer JDKs (e.g. 25) close off
        unitTests.all {
            it.jvmArgs(
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED",
            )
        }
    }
}

dependencies {
    implementation(libs.osmdroidAndroid)

    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

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
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    implementation(libs.coil3.compose)
    implementation(libs.coil3.network.ktor)

    implementation(libs.glance.appwidget)
    implementation(libs.androidx.remote.core)
    implementation(libs.androidx.remote.creation)
    implementation(libs.androidx.remote.creation.compose)
    implementation(libs.androidx.remote.player.view)
    implementation(libs.androidx.remote.player.core)
    implementation(libs.androidx.remote.player.compose)
    implementation(libs.androidx.remote.tooling.preview)
    implementation(libs.androidx.wear.remote.material3)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Compose testing dependencies
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    constraints {
        // androidx.test.ext:truth 1.7.0 needs 1.2.0; align main variant so the
        // androidTest classpath (pinned to main variant versions) can resolve
        implementation("androidx.concurrent:concurrent-futures:1.2.0")
        implementation("androidx.concurrent:concurrent-futures-ktx:1.2.0")
    }


    implementation(projects.common)
}

