buildscript {
    // kotlin-native-nuget 0.6.0 brings KSP 2.3.10, which calls KotlinNativeCompile.konanHome (removed in Kotlin 2.5)
    configurations.classpath { resolutionStrategy.force("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.12") }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.gradleVersionsPlugin) apply false
    alias(libs.plugins.shadowPlugin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlin.native.nuget) apply false
}

// force patched versions of vulnerable transitive npm deps of the wasm webpack tooling
plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension>().apply {
        resolution("ws", "8.21.0")
        resolution("serialize-javascript", "7.0.5")
    }
}

val kotlinVersion = libs.versions.kotlin.get()
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
            force("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
            force("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
        }
    }
}
