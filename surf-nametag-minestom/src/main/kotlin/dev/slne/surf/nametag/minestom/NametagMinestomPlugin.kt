package dev.slne.surf.nametag.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.nametag.core.client.config.NametagConfig
import dev.slne.surf.nametag.core.client.hook.ClanHook
import dev.slne.surf.nametag.core.client.hook.ContentCreatorHook
import dev.slne.surf.nametag.minestom.hook.registerLuckPermsListeners
import dev.slne.surf.nametag.minestom.listener.NametagListener

object NametagMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        NametagConfig.init()

        registerLuckPermsListeners()
        ClanHook.createListeners()
        ContentCreatorHook.registerListener()
    }

    override suspend fun onEnable() {
        NametagListener.register(eventNode)
    }
}
