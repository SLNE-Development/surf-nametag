plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.nametag.PaperMain")
    authors.add("red")
    foliaSupported(true)

    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-nametag-api"))
}