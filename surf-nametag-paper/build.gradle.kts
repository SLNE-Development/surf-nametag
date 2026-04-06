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
        registerSoft("LuckPerms")
    }
}

dependencies {
    api(projects.surfNametagApi)
    compileOnly("net.luckperms:api:5.4")
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
}