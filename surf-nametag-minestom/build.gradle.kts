import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

repositories {
    mavenLocal()
}

minestomPluginFile {
    main = "dev.slne.surf.nametag.minestom.NametagMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-clan-minestom")
        register("surf-content-creator-minestom")
    }
}

dependencies {
    api(projects.surfNametagCoreClient)

    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}

publishing {
    repositories {
        slneReleases()
    }
}
