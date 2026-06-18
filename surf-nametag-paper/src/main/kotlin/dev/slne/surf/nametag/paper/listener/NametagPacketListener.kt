package dev.slne.surf.nametag.paper.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.*
import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.jvm.optionals.getOrNull

object NametagPacketListener : PacketListenerAbstract(PacketListenerPriority.HIGH) {
    override fun onPacketSend(event: PacketSendEvent) {
        when (event.packetType) {
            PacketType.Play.Server.SET_PASSENGERS -> handleSetPassengers(event)
            PacketType.Play.Server.SPAWN_ENTITY -> handleSpawnEntity(event)
            PacketType.Play.Server.DESTROY_ENTITIES -> handleDestroyEntities(event)
            PacketType.Play.Server.ENTITY_METADATA -> handleUpdateMetaData(event)
        }
    }

    private fun handleUpdateMetaData(event: PacketSendEvent) {
        val viewer = event.getPlayer() as? Player ?: return
        val wrapper = WrapperPlayServerEntityMetadata(event)
        val player = findPlayerByEntityId(wrapper.entityId) ?: return
        val entityData =
            wrapper.entityMetadata.find { it.index == 0 && it.type == EntityDataTypes.BYTE }
                ?: return

        val flagsByte = entityData.value as Byte
        val isInvisible = (flagsByte.toInt() and 0x20) != 0

        if (isInvisible) {
            nametagService.handlePlayerUntracked(wrapper.entityId, viewer)
        } else {
            nametagService.handlePlayerTracked(player, viewer)
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

        val uuid = wrapper.uuid.getOrNull() ?: return
        val spawnedPlayer = Bukkit.getPlayer(uuid) ?: return

        nametagService.ensureTeamMembership(viewer, spawnedPlayer.name)
        nametagService.handlePlayerTracked(spawnedPlayer, viewer)
    }

    private fun handleDestroyEntities(event: PacketSendEvent) {
        val viewer = event.getPlayer() as? Player ?: return
        val wrapper = WrapperPlayServerDestroyEntities(event)

        for (entityId in wrapper.entityIds) {
            nametagService.handlePlayerUntracked(entityId, viewer)
        }
    }


    fun findPlayerByEntityId(entityId: Int) =
        Bukkit.getOnlinePlayers().firstOrNull { it.entityId == entityId }
}
