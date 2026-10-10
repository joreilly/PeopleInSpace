@file:OptIn(ExperimentalExportDsl::class)

import org.jetbrains.kotlin.gradle.export.ExperimentalExportDsl

// Umbrella for Swift Export's new `export { swift { } }` DSL, which exports this module's whole
// compile classpath. Depending on :common here limits that to :common plus its `api` deps.
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
            xcodeIntegration()
        }
    }
}

// WORKAROUND (Swift Export, Kotlin 2.5.0-Beta1): when a declaration uses a type the exporter
// cannot express (here, Koin's generated `Module.module()` extensions whose receivers are
// internal), it erases the receiver to Swift.Never and marks the stub @available(*, unavailable).
// It does not deduplicate them, so N such declarations collide as `invalid redeclaration`. They
// are unusable by construction, so keep only the first of each identical signature.
// (The duplicate @_spi stub defect this also used to patch, KT-87791, is fixed in 2.5.0-Beta1.)
val swiftExportOutputDirs = listOf(
    layout.buildDirectory.dir("SPMPackage"),
    layout.buildDirectory.dir("SwiftExport"),
).map { it.get().asFile }

tasks.matching { it.name.endsWith("GenerateSPMPackage") }.configureEach {
    val dirs = swiftExportOutputDirs
    doLast {
        var removed = 0
        dirs.flatMap { root ->
            if (root.exists()) root.walkTopDown().filter { it.name.endsWith(".swift") }.toList() else emptyList()
        }.forEach { f ->
            val lines = f.readText().split("\n")
            val seenUnavailable = mutableSetOf<String>()
            val drop = sortedSetOf<Int>()
            var k = 0
            while (k < lines.size) {
                if (lines[k].trim().startsWith("@available(*, unavailable")) {
                    var e = k + 1
                    while (e < lines.size && lines[e].trim() != "}") e++
                    val sig = (k..minOf(e, lines.size - 1)).joinToString("\n") { lines[it] }
                    if (!seenUnavailable.add(sig)) {
                        (k..e).forEach { drop.add(it) }
                        removed++
                    }
                    k = e + 1
                    continue
                }
                k++
            }
            if (drop.isNotEmpty()) f.writeText(lines.filterIndexed { i, _ -> i !in drop }.joinToString("\n"))
        }
        if (removed > 0) println("Swift Export: removed $removed duplicate unavailable stub(s)")
    }
}
