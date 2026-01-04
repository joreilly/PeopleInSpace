pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            mavenCentral()
            gradlePluginPortal()
            maven {
                url = uri("https://androidx.dev/snapshots/builds/14637376/artifacts/repository")
            }
        }
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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":wearApp")
include(":compose-desktop")
include(":compose-web")
include(":common")
include(":backend")
include(":mcp-server")
