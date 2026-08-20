package dev.slne.surf.nametag.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.nametag.core.client.service.NametagService
import dev.slne.surf.nametag.paper.platform.PaperNametagPlayer
import dev.slne.surf.nametag.paper.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerHideEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerShowEntityEvent

object NametagListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch {
            NametagService.handleJoin(PaperNametagPlayer(event.player))
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        NametagService.handleLeave(PaperNametagPlayer(event.player))
    }

    @EventHandler
    fun onHide(event: PlayerHideEntityEvent) {
        val hiddenPlayer = event.entity as? Player ?: return
        NametagService.handleLeave(
            PaperNametagPlayer(hiddenPlayer),
            PaperNametagPlayer(event.player)
        )
    }

    @EventHandler
    fun onShow(event: PlayerShowEntityEvent) {
        val shownPlayer = event.entity as? Player ?: return

        plugin.launch {
            NametagService.handleJoin(
                PaperNametagPlayer(shownPlayer),
                PaperNametagPlayer(event.player)
            )
        }
    }
}
