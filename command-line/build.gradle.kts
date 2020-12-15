plugins {
    kotlin("multiplatform")
}

kotlin {
    macosX64("native") {
        binaries {
            executable {
                linkerOpts.add("-lsqlite3")
            }
        }
    }
}

dependencies {
    commonMainImplementation(project(":common"))
}
