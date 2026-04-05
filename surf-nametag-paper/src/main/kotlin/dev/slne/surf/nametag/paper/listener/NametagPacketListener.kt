package dev.slne.surf.nametag.paper.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object NametagPacketListener : PacketListenerAbstract(PacketListenerPriority.HIGH) {
    override fun onPacketSend(event: PacketSendEvent) {
        when (event.packetType) {
            PacketType.Play.Server.SET_PASSENGERS -> handleSetPassengers(event)
            PacketType.Play.Server.SPAWN_ENTITY -> handleSpawnEntity(event)
        }
    }

    private fun handleSetPassengers(event: PacketSendEvent) {
        val viewer = event.getPlayer() as? Player ?: return
        val wrapper = WrapperPlayServerSetPassengers(event)

        val virtualId = nametagService.getVirtualPassengerId(
            wrapper.entityId, viewer.uniqueId
        ) ?: return

        val passengers = wrapper.passengers
        if (virtualId !in passengers) {
            wrapper.passengers = passengers + virtualId
        }
    }

    private fun handleSpawnEntity(event: PacketSendEvent) {
        val viewer = event.getPlayer() as? Player ?: return
        val wrapper = WrapperPlayServerSpawnEntity(event)
        val spawnedPlayer = Bukkit.getPlayer(wrapper.entityUUID) ?: return

        nametagService.ensureTeamMembership(viewer, spawnedPlayer.name)
    }
}
