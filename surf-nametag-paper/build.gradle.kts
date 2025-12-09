import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.nametag.paper.PaperMain")
    authors.add("red")
    foliaSupported(true)
    generateLibraryLoader(false)

    serverDependencies {
        registerSoft("LuckPerms")
    }
}

dependencies {
    api(project(":surf-nametag-api"))
    compileOnly("net.luckperms:api:5.4")
}