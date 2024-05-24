pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
       // maven(url = "https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "OPSC7311_POE"
include(":app")
