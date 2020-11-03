pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

}

include(":backend")
rootProject.name = "PeopleInSpace"

enableFeaturePreview("GRADLE_METADATA")

include(":common", ":compose")