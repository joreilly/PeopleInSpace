@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.sqlDelight)
}

// The SQLDelight-generated database classes are public declarations, and Swift Export exports
// every public declaration of the module it is applied to -- referenced or not. Keeping them
// out of :common is what keeps app.cash.sqldelight (and its generic SqlSchema<T : QueryResult<Unit>>,
// which Swift Export cannot currently express) out of the exported surface. :common depends on
// this module with `implementation` and touches it only from internal code.
kotlin {
    explicitApi()

    jvmToolchain(17)

    iosArm64()
    iosSimulatorArm64()
    mingwX64()
    jvm()

    android {
        namespace = "dev.johnoreilly.common.db"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.sqldelight.runtime)
            api(libs.sqldelight.coroutines.extensions)
        }
        androidMain.dependencies { implementation(libs.sqldelight.android.driver) }
        jvmMain.dependencies { implementation(libs.sqldelight.sqlite.driver) }
        appleMain.dependencies { implementation(libs.sqldelight.native.driver) }
        mingwMain.dependencies { implementation(libs.sqldelight.native.driver) }
        wasmJsMain.dependencies {
            implementation(libs.sqldelight.web.driver)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", libs.versions.sqlJs.get()))
            implementation(devNpm("copy-webpack-plugin", libs.versions.webPackPlugin.get()))
        }
    }
}

sqldelight {
    databases {
        create("PeopleInSpaceDatabase") {
            generateAsync = true
            packageName.set("dev.johnoreilly.peopleinspace.db")
        }
    }
}
