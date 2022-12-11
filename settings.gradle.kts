pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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

include(":app", ":common")
//include(":compose-desktop")
//include(":compose-ios")
include(":wearApp")
include(":wearApp-benchmark")
include(":web")
//include(":compose-web")
include(":backend")
include(":graphql-server")
