plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.gradleVersionsPlugin) apply false
    alias(libs.plugins.shadowPlugin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-test:2.2.21")
            force("org.jetbrains.kotlin:kotlin-test-common:2.2.21")
            force("org.jetbrains.kotlin:kotlin-test-annotations-common:2.2.21")
        }
    }
}
