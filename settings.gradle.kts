include("surf-nametag-api")
include("surf-nametag-core-client")
include("surf-nametag-paper")
include("surf-nametag-minestom")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}
