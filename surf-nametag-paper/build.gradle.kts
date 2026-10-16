import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.nametag.paper.PaperMain")
    authors.add("red")
    foliaSupported(true)
    generateLibraryLoader(false)

    serverDependencies {
        registerSoft("surf-clan-paper")
        registerSoft("surf-content-creator-paper")
    }
}

dependencies {
    api(projects.surfNametagCoreClient)
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}