buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.10+")
    }
}

allprojects {
    version = findProperty("version") as String
    group = "dev.slne.surf.nametag"
}