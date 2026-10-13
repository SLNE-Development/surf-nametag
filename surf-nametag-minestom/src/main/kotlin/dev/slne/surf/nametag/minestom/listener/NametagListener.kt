package dev.slne.surf.nametag.minestom.listener

import dev.slne.surf.api.minestom.coroutine.minestomScope
import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.nametag.core.client.service.NametagService
import dev.slne.surf.nametag.minestom.platform.MinestomNametagPlayer
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

/**
 * Keeps the nametag teams of a player in sync with them arriving and leaving.
 */
object NametagListener : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            val player = MinestomNametagPlayer(event.player)

            minestomScope.launch {
                NametagService.handleJoin(player)
            }
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            val player = MinestomNametagPlayer(event.player)

            minestomScope.launch {
                NametagService.handleLeave(player)
            }
        }
    }
}
