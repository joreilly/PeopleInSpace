pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            mavenCentral()
            gradlePluginPortal()
            maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
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

include(":app")
include(":wearApp")
include(":compose-desktop")
include(":compose-web")
include(":common")
include(":backend")
include(":graphql-server")
