pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.google.cloud.tools.appengine")) {
                useModule("com.google.cloud.tools:appengine-gradle-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "PeopleInSpace"

include(":app")
include(":common", ":compose-desktop")
include(":compose-web")
include(":compose-web-wasm")
include(":compose-ios")
include(":wearApp")
include(":wearApp-benchmark")
include(":web")
include(":backend")
include(":graphql-server")
