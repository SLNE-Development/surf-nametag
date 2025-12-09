package dev.slne.surf.nametag.paper.listener

import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object NametagListener : Listener {
    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        nametagService.handleJoin(event.player)
    }
}