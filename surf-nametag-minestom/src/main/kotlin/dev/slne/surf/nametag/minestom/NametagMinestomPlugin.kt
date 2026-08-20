package dev.slne.surf.nametag.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.nametag.minestom.listener.NametagListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-nametag-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-clan-minestom",
        "surf-content-creator-minestom"
    ]
)
class NametagMinestomPlugin : MinestomPlugin(NametagMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindEventRegistrar<NametagListener>()
    }
}
