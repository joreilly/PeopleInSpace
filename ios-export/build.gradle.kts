@file:OptIn(ExperimentalSwiftExportDsl::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.export.SwiftExportVisibility
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

// Umbrella for Swift Export's `export { swift { } }` DSL. What gets exported follows the
// api/implementation split: :common (an `api` dep here) plus whatever :common exposes via `api`.
// Dependencies Swift doesn't use directly are HIDDEN -- they shrink to stubs for the few types
// :common's API mentions. Careful: a function returning a hidden type is generated as a
// fatalError() stub with no warning (see the Unit-returning initKoin()).
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.common)
        }
    }

    export {
        swift {
            moduleName = "Shared"
            xcodeIntegration {
                configure(projects.common) {
                    moduleName = "Common"
                    rootPackage = "dev.johnoreilly.common"
                }
                configure("io.insert-koin:koin-core", SwiftExportVisibility.HIDDEN)
                configure("org.jetbrains.kotlinx:kotlinx-serialization-core", SwiftExportVisibility.HIDDEN)
                configure("androidx.lifecycle:lifecycle-viewmodel", SwiftExportVisibility.HIDDEN)
            }
        }
    }
}
