package dev.slne.surf.nametag.paper.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers
import dev.slne.surf.nametag.paper.service.nametagService
import org.bukkit.entity.Player

object NametagPacketListener : PacketListenerAbstract(PacketListenerPriority.HIGH) {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType != PacketType.Play.Server.SET_PASSENGERS) return

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
}
