package dev.slne.surf.nametag.paper.listener

import dev.slne.surf.nametag.api.surfNametagApi
import dev.slne.surf.nametag.paper.service.nametagService
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent

object NametagListener : Listener {
    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        nametagService.handleJoin(event.player)
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        forEachPlayer {
            surfNametagApi.hideNametag(event.player.uniqueId, it.uniqueId)
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        forEachPlayer {
            surfNametagApi.showNametag(event.player.uniqueId, it.uniqueId)
        }
    }
}