package dev.slne.surf.nametag.paper.listener

import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object NametagListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        nametagService.handleJoin(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        nametagService.handleQuit(event.player)
    }
}