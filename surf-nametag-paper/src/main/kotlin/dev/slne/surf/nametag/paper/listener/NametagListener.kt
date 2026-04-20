package dev.slne.surf.nametag.paper.listener

import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerHideEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerShowEntityEvent
import org.bukkit.entity.Player

object NametagListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        nametagService.handleJoin(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        nametagService.handleQuit(event.player)
    }

    @EventHandler
    fun onHide(event: PlayerHideEntityEvent) {
        val hiddenPlayer = event.entity as? Player ?: return
        nametagService.hideNametag(hiddenPlayer.uniqueId, event.player.uniqueId)
    }

    @EventHandler
    fun onShow(event: PlayerShowEntityEvent) {
        val shownPlayer = event.entity as? Player ?: return
        nametagService.showNametag(shownPlayer.uniqueId, event.player.uniqueId)
    }
}