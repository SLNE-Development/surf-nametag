package dev.slne.surf.nametag.paper.hook

import dev.slne.surf.content.creator.api.ContentCreatorApi
import dev.slne.surf.content.creator.api.ContentCreatorPlatform
import dev.slne.surf.content.creator.api.listener.StateChangeListener
import dev.slne.surf.content.creator.api.platform.PlatformState
import dev.slne.surf.nametag.paper.service.nametagService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

object ContentCreatorHook {
    fun renderLiveTag(playerUuid: UUID): Component =
        ContentCreatorApi.renderLiveTag(playerUuid, space = true)

    fun registerListener() {
        ContentCreatorApi.registerStateChangeListener(object : StateChangeListener {
            override fun onStateChanged(
                playerUuid: UUID,
                contentCreatorPlatform: ContentCreatorPlatform,
                newState: PlatformState
            ) {
                Bukkit.getPlayer(playerUuid)?.let {
                    nametagService.handleDataUpdate(it)
                }
            }
        })
    }
}